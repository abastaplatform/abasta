package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.User;
import cat.abasta_back_end.entities.Company.CompanyStatus;
import cat.abasta_back_end.entities.User.UserRole;
import cat.abasta_back_end.exceptions.BadRequestException;
import cat.abasta_back_end.exceptions.DuplicateResourceException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    // ==================== Tests de RegisterUser ====================

    @Test
    @DisplayName("RegisterUser hauria de crear un usuari nou correctament")
    void registerUser_ShouldCreateNewUser_WhenDataIsValid() {
        // Given
        mockAuthenticatedAdmin();

        UserRegistrationDTO registrationDTO = UserRegistrationDTO.builder()
                .email("nou@abasta.com")
                .password("Password123")
                .firstName("Maria")
                .lastName("Martínez")
                .phone("600123456")
                .role(UserRole.USER)
                .build();

        User newUser = User.builder()
                .id(2L)
                .uuid("new-user-uuid")
                .email("nou@abasta.com")
                .firstName("Maria")
                .lastName("Martínez")
                .phone("600123456")
                .role(UserRole.USER)
                .company(testCompany)
                .isActive(true)
                .emailVerified(false)
                .build();

        when(userRepository.existsByEmail("nou@abasta.com")).thenReturn(false);
        when(companyRepository.findByUuid("company-uuid")).thenReturn(Optional.of(testCompany));
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-password-123");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        UserResponseDTO response = userService.registerUser(registrationDTO);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("nou@abasta.com");
        assertThat(response.getFirstName()).isEqualTo("Maria");
        assertThat(response.getLastName()).isEqualTo("Martínez");

        verify(emailService).sendEmailVerification(eq("nou@abasta.com"), anyString(), eq("Maria"));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("RegisterUser hauria de llançar excepció si l'usuari no és ADMIN")
    void registerUser_ShouldThrowException_WhenUserIsNotAdmin() {
        // Given
        mockAuthenticatedNonAdmin();

        UserRegistrationDTO registrationDTO = UserRegistrationDTO.builder()
                .email("nou@abasta.com")
                .password("Password123")
                .firstName("Maria")
                .lastName("Martínez")
                .build();

        // When & Then
        assertThatThrownBy(() -> userService.registerUser(registrationDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("L'usuari ha de ser Administrador");
    }

    @Test
    @DisplayName("RegisterUser hauria de llançar excepció si l'email ja existeix")
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        mockAuthenticatedAdmin();

        UserRegistrationDTO registrationDTO = UserRegistrationDTO.builder()
                .email("test@abasta.com")
                .password("Password123")
                .firstName("Maria")
                .lastName("Martínez")
                .build();

        when(userRepository.existsByEmail("test@abasta.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.registerUser(registrationDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Ja existeix un usuari amb l'email");
    }

    @Test
    @DisplayName("RegisterUser hauria de llançar excepció si l'empresa no existeix")
    void registerUser_ShouldThrowException_WhenCompanyNotFound() {
        // Given
        mockAuthenticatedAdmin();

        UserRegistrationDTO registrationDTO = UserRegistrationDTO.builder()
                .email("nou@abasta.com")
                .password("Password123")
                .firstName("Maria")
                .lastName("Martínez")
                .build();

        when(userRepository.existsByEmail("nou@abasta.com")).thenReturn(false);
        when(companyRepository.findByUuid("company-uuid")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.registerUser(registrationDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Empresa no trobada");
    }

    // ==================== Tests de GetAllUsersPaginated ====================

    @Test
    @DisplayName("GetAllUsersPaginated hauria de retornar usuaris paginats")
    void getAllUsersPaginated_ShouldReturnPagedUsers() {
        // Given
        mockAuthenticatedAdmin();

        User user2 = User.builder()
                .id(2L)
                .uuid("user-uuid-2")
                .email("user2@abasta.com")
                .firstName("Pere")
                .lastName("López")
                .role(UserRole.USER)
                .company(testCompany)
                .isActive(true)
                .isDeleted(false)
                .build();

        List<User> users = Arrays.asList(testUser, user2);
        Page<User> usersPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByCompanyUuidAndIsDeletedFalse("company-uuid", pageable)).thenReturn(usersPage);

        // When
        Page<UserResponseDTO> result = userService.getAllUsersPaginated(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        verify(userRepository).findByCompanyUuidAndIsDeletedFalse("company-uuid", pageable);
    }

    @Test
    @DisplayName("GetAllUsersPaginated hauria de llançar excepció si no és ADMIN")
    void getAllUsersPaginated_ShouldThrowException_WhenUserIsNotAdmin() {
        // Given
        mockAuthenticatedNonAdmin();
        Pageable pageable = PageRequest.of(0, 10);

        // When & Then
        assertThatThrownBy(() -> userService.getAllUsersPaginated(pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("L'usuari ha de ser Administrador");
    }

    // ==================== Tests de GetUserByUuid ====================

    @Test
    @DisplayName("GetUserByUuid hauria de retornar l'usuari correcte")
    void getUserByUuid_ShouldReturnUser_WhenUserExists() {
        // Given
        mockAuthenticatedAdmin();
        when(userRepository.findByUuid("user-uuid")).thenReturn(Optional.of(testUser));

        // When
        UserResponseDTO result = userService.getUserByUuid("user-uuid");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo("user-uuid");
        assertThat(result.getEmail()).isEqualTo("test@abasta.com");
        verify(userRepository).findByUuid("user-uuid");
    }

    @Test
    @DisplayName("GetUserByUuid hauria de llançar excepció si l'usuari no existeix")
    void getUserByUuid_ShouldThrowException_WhenUserNotFound() {
        // Given
        mockAuthenticatedAdmin();
        when(userRepository.findByUuid("invalid-uuid")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserByUuid("invalid-uuid"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuari no trobat");
    }

    @Test
    @DisplayName("GetUserByUuid hauria de llançar excepció si no és ADMIN")
    void getUserByUuid_ShouldThrowException_WhenUserIsNotAdmin() {
        // Given
        mockAuthenticatedNonAdmin();

        // When & Then
        assertThatThrownBy(() -> userService.getUserByUuid("user-uuid"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("L'usuari ha de ser Administrador");
    }

    // ==================== Tests de SearchUsersByText ====================

    @Test
    @DisplayName("SearchUsersByText hauria de retornar usuaris que coincideixin amb el text")
    void searchUsersByText_ShouldReturnMatchingUsers() {
        // Given
        mockAuthenticatedAdmin();

        List<User> users = Collections.singletonList(testUser);
        Page<User> usersPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        when(companyRepository.findByUuid("company-uuid")).thenReturn(Optional.of(testCompany));
        when(userRepository.findByCompanyIdAndMultipleFieldsContainingNoDeleted(1L, "Joan", pageable))
                .thenReturn(usersPage);

        // When
        Page<UserResponseDTO> result = userService.searchUsersByText("Joan", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getFirstName()).isEqualTo("Joan");
    }

    @Test
    @DisplayName("SearchUsersByText hauria de llançar excepció si l'empresa no existeix")
    void searchUsersByText_ShouldThrowException_WhenCompanyNotFound() {
        // Given
        mockAuthenticatedAdmin();
        Pageable pageable = PageRequest.of(0, 10);

        when(companyRepository.findByUuid("company-uuid")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.searchUsersByText("Joan", pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Empresa no trobada");
    }

    // ==================== Tests de SearchUsersWithFilters ====================

    @Test
    @DisplayName("SearchUsersWithFilters hauria de retornar usuaris filtrats")
    void searchUsersWithFilters_ShouldReturnFilteredUsers() {
        // Given
        mockAuthenticatedAdmin();

        UserFilterDTO filterDTO = UserFilterDTO.builder()
                .email("test@abasta.com")
                .isActive(true)
                .build();

        List<User> users = Collections.singletonList(testUser);
        Page<User> usersPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        when(companyRepository.findByUuid("company-uuid")).thenReturn(Optional.of(testCompany));
        when(userRepository.findByCompanyIdAndCriteriaActive(
                eq(1L), eq("test@abasta.com"), isNull(), isNull(), isNull(), eq(true), eq(pageable)))
                .thenReturn(usersPage);

        // When
        Page<UserResponseDTO> result = userService.searchUsersWithFilters(filterDTO, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("test@abasta.com");
    }

    // ==================== Tests de UpdateUser ====================

    @Test
    @DisplayName("UpdateUser hauria d'actualitzar l'usuari correctament")
    void updateUser_ShouldUpdateUser_WhenDataIsValid() {
        // Given
        mockAuthenticatedAdmin();

        UserRequestDTO updateDTO = UserRequestDTO.builder()
                .email("updated@abasta.com")
                .firstName("Joan")
                .lastName("Garcia Updated")
                .phone("600999888")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        when(userRepository.findByUuid("user-uuid")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@abasta.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponseDTO result = userService.updateUser("user-uuid", updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(testUser);
        assertThat(testUser.getEmail()).isEqualTo("updated@abasta.com");
        assertThat(testUser.getLastName()).isEqualTo("Garcia Updated");
    }

    @Test
    @DisplayName("UpdateUser hauria de llançar excepció si l'email ja existeix")
    void updateUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        mockAuthenticatedAdmin();

        UserRequestDTO updateDTO = UserRequestDTO.builder()
                .email("existing@abasta.com")
                .firstName("Joan")
                .lastName("Garcia")
                .build();

        when(userRepository.findByUuid("user-uuid")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@abasta.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateUser("user-uuid", updateDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Ja existeix un usuari amb l'email");
    }

    @Test
    @DisplayName("UpdateUser hauria de llançar excepció si no és ADMIN")
    void updateUser_ShouldThrowException_WhenUserIsNotAdmin() {
        // Given
        mockAuthenticatedNonAdmin();

        UserRequestDTO updateDTO = UserRequestDTO.builder()
                .email("updated@abasta.com")
                .firstName("Joan")
                .lastName("Garcia")
                .build();

        // When & Then
        assertThatThrownBy(() -> userService.updateUser("user-uuid", updateDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("L'usuari ha de ser Administrador");
    }

    // ==================== Tests de ChangeUserStatus ====================

    @Test
    @DisplayName("ChangeUserStatus hauria de canviar l'estat de l'usuari")
    void changeUserStatus_ShouldChangeStatus_WhenDataIsValid() {
        // Given
        mockAuthenticatedAdmin();
        when(userRepository.findByUuid("user-uuid")).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        UserResponseDTO result = userService.changeUserStatus("user-uuid", false);

        // Then
        assertThat(result).isNotNull();
        assertThat(testUser.getIsActive()).isFalse();
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("ChangeUserStatus hauria de llançar excepció si no és ADMIN")
    void changeUserStatus_ShouldThrowException_WhenUserIsNotAdmin() {
        // Given
        mockAuthenticatedNonAdmin();

        // When & Then
        assertThatThrownBy(() -> userService.changeUserStatus("user-uuid", false))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("L'usuari ha de ser Administrador");
    }

    // ==================== Tests de ChangePassword ====================

    @Test
    @DisplayName("ChangePassword hauria de canviar la contrasenya correctament")
    void changePassword_ShouldChangePassword_WhenCurrentPasswordIsCorrect() {
        // Given
        PasswordChangeDTO changeDTO = PasswordChangeDTO.builder()
                .currentPassword("oldPassword123")
                .newPassword("newPassword456")
                .build();

        when(userRepository.findByUuid("user-uuid")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword123", "encoded-password")).thenReturn(true);
        when(passwordEncoder.encode("newPassword456")).thenReturn("new-encoded-password");

        // When
        userService.changePassword("user-uuid", changeDTO);

        // Then
        verify(passwordEncoder).encode("newPassword456");
        verify(userRepository).save(testUser);
        assertThat(testUser.getPassword()).isEqualTo("new-encoded-password");
    }

    @Test
    @DisplayName("ChangePassword hauria de llançar excepció si la contrasenya actual és incorrecta")
    void changePassword_ShouldThrowException_WhenCurrentPasswordIsWrong() {
        // Given
        PasswordChangeDTO changeDTO = PasswordChangeDTO.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword456")
                .build();

        when(userRepository.findByUuid("user-uuid")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encoded-password")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword("user-uuid", changeDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("La contrasenya actual és incorrecta");
    }

    @Test
    @DisplayName("ChangePassword hauria de llançar excepció si l'usuari no existeix")
    void changePassword_ShouldThrowException_WhenUserNotFound() {
        // Given
        PasswordChangeDTO changeDTO = PasswordChangeDTO.builder()
                .currentPassword("oldPassword123")
                .newPassword("newPassword456")
                .build();

        when(userRepository.findByUuid("invalid-uuid")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.changePassword("invalid-uuid", changeDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuari no trobat");
    }

    // ==================== Tests de DeleteUser ====================

    @Test
    @DisplayName("DeleteUser hauria de fer soft delete de l'usuari")
    void deleteUser_ShouldSoftDeleteUser() {
        // Given
        mockAuthenticatedAdmin();
        when(userRepository.findByUuid("user-uuid")).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        userService.deleteUser("user-uuid");

        // Then
        assertThat(testUser.getIsDeleted()).isTrue();
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("DeleteUser hauria de llançar excepció si no és ADMIN")
    void deleteUser_ShouldThrowException_WhenUserIsNotAdmin() {
        // Given
        mockAuthenticatedNonAdmin();

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser("user-uuid"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("L'usuari ha de ser Administrador");
    }

    @Test
    @DisplayName("DeleteUser hauria de llançar excepció si l'usuari no existeix")
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        mockAuthenticatedAdmin();
        when(userRepository.findByUuid("invalid-uuid")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser("invalid-uuid"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuari no trobat");
    }

    // ==================== Mètodes auxiliars per mocking ====================

    private void mockAuthenticatedAdmin() {
        User adminUser = User.builder()
                .id(1L)
                .uuid("admin-uuid")
                .email("admin@abasta.com")
                .role(UserRole.ADMIN)
                .company(testCompany)
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@abasta.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("admin@abasta.com")).thenReturn(Optional.of(adminUser));
    }

    private void mockAuthenticatedNonAdmin() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@abasta.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("test@abasta.com")).thenReturn(Optional.of(testUser));
    }
}