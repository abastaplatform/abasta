package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.LoginRequestDTO;
import cat.abasta_back_end.dto.LoginResponseDTO;
import cat.abasta_back_end.dto.PasswordResetDTO;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.User;
import cat.abasta_back_end.entities.Company.CompanyStatus;
import cat.abasta_back_end.entities.User.UserRole;
import cat.abasta_back_end.exceptions.BadRequestException;
import cat.abasta_back_end.repositories.CompanyRepository;
import cat.abasta_back_end.repositories.UserRepository;
import cat.abasta_back_end.security.JwtUtil;
import cat.abasta_back_end.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris simplificats per UserServiceImpl.
 * Verifica la lògica de negoci d'autenticació, recuperació de password i verificació d'email.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Company testCompany;
    private LoginRequestDTO loginRequest;

    @BeforeEach
    void setUp() {
        testCompany = Company.builder()
                .id(1L)
                .uuid("company-uuid")
                .name("Test Company")
                .status(CompanyStatus.ACTIVE)
                .build();

        testUser = User.builder()
                .id(1L)
                .uuid("user-uuid")
                .email("test@abasta.com")
                .password("encoded-password")
                .firstName("Joan")
                .lastName("Garcia")
                .role(UserRole.USER)
                .company(testCompany)
                .isActive(true)
                .emailVerified(true)
                .build();

        loginRequest = LoginRequestDTO.builder()
                .email("test@abasta.com")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("Login hauria de retornar token JWT quan les credencials són correctes")
    void login_ShouldReturnJwtToken_WhenCredentialsAreValid() {
        // Given
        when(userRepository.findByEmail("test@abasta.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(jwtUtil.generateToken("test@abasta.com")).thenReturn("jwt-token");

        // When
        LoginResponseDTO response = userService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUser().getEmail()).isEqualTo("test@abasta.com");

        verify(userRepository).save(testUser);
        verify(jwtUtil).generateToken("test@abasta.com");
    }

    @Test
    @DisplayName("Login hauria de llançar excepció quan l'usuari no existeix")
    void login_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findByEmail("test@abasta.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Credencials invàlides");
    }

    @Test
    @DisplayName("Login hauria de llançar excepció quan l'usuari està inactiu")
    void login_ShouldThrowException_WhenUserIsInactive() {
        // Given
        testUser.setIsActive(false);
        when(userRepository.findByEmail("test@abasta.com")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("L'usuari està inactiu");
    }

    @Test
    @DisplayName("Login hauria de llançar excepció quan l'email no està verificat")
    void login_ShouldThrowException_WhenEmailNotVerified() {
        // Given
        testUser.setEmailVerified(false);
        when(userRepository.findByEmail("test@abasta.com")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("verificar el teu correu electrònic");
    }

    @Test
    @DisplayName("Login hauria de llançar excepció quan la contrasenya és incorrecta")
    void login_ShouldThrowException_WhenPasswordIsWrong() {
        // Given
        when(userRepository.findByEmail("test@abasta.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Credencials invàlides");
    }

    @Test
    @DisplayName("RequestPasswordReset hauria de generar token i enviar email")
    void requestPasswordReset_ShouldGenerateTokenAndSendEmail() {
        // Given
        when(userRepository.findByEmail("test@abasta.com")).thenReturn(Optional.of(testUser));

        // When
        userService.requestPasswordReset("test@abasta.com");

        // Then
        verify(userRepository).save(testUser);
        verify(emailService).sendPasswordResetEmail(eq("test@abasta.com"), anyString(), eq("Joan"));
        assertThat(testUser.getPasswordResetToken()).isNotNull();
        assertThat(testUser.getPasswordResetExpires()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("ResetPassword hauria de canviar la contrasenya amb token vàlid")
    void resetPassword_ShouldChangePassword_WhenTokenIsValid() {
        // Given
        PasswordResetDTO resetDTO = PasswordResetDTO.builder()
                .token("valid-token")
                .newPassword("newPassword123")
                .build();

        when(userRepository.findByValidResetToken(eq("valid-token"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("new-encoded-password");

        // When
        userService.resetPassword(resetDTO);

        // Then
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(testUser);
        assertThat(testUser.getPassword()).isEqualTo("new-encoded-password");
        assertThat(testUser.getPasswordResetToken()).isNull();
        assertThat(testUser.getPasswordResetExpires()).isNull();
    }

    @Test
    @DisplayName("VerifyEmail hauria de verificar l'usuari i activar l'empresa si és ADMIN")
    void verifyEmail_ShouldVerifyUserAndActivateCompany_WhenUserIsAdmin() {
        // Given
        testUser.setRole(UserRole.ADMIN);
        testUser.setEmailVerified(false);
        testCompany.setStatus(CompanyStatus.PENDING);

        when(userRepository.findByValidVerificationToken(eq("valid-token"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(testUser));

        // When
        userService.verifyEmail("valid-token");

        // Then
        assertThat(testUser.getEmailVerified()).isTrue();
        assertThat(testUser.getEmailVerificationToken()).isNull();
        assertThat(testCompany.getStatus()).isEqualTo(CompanyStatus.ACTIVE);

        verify(userRepository).save(testUser);
        verify(companyRepository).save(testCompany);
    }

    @Test
    @DisplayName("VerifyEmail hauria de verificar l'usuari sense activar empresa si és USER")
    void verifyEmail_ShouldVerifyUserOnly_WhenUserIsNotAdmin() {
        // Given
        testUser.setRole(UserRole.USER);
        testUser.setEmailVerified(false);

        when(userRepository.findByValidVerificationToken(eq("valid-token"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(testUser));

        // When
        userService.verifyEmail("valid-token");

        // Then
        assertThat(testUser.getEmailVerified()).isTrue();
        verify(userRepository).save(testUser);
        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("ResendVerificationEmail hauria de generar nou token i enviar email")
    void resendVerificationEmail_ShouldGenerateNewTokenAndSendEmail() {
        // Given
        testUser.setEmailVerified(false);
        when(userRepository.findByEmail("test@abasta.com")).thenReturn(Optional.of(testUser));

        // When
        userService.resendVerificationEmail("test@abasta.com");

        // Then
        verify(emailService).sendEmailVerification(eq("test@abasta.com"), anyString(), eq("Joan"));
        verify(userRepository).save(testUser);
        assertThat(testUser.getEmailVerificationToken()).isNotNull();
        assertThat(testUser.getEmailVerificationExpires()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("ResendVerificationEmail hauria de llançar excepció si l'email ja està verificat")
    void resendVerificationEmail_ShouldThrowException_WhenEmailAlreadyVerified() {
        // Given
        testUser.setEmailVerified(true);
        when(userRepository.findByEmail("test@abasta.com")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userService.resendVerificationEmail("test@abasta.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Aquest email ja està verificat");
    }
}