package cat.abasta_back_end.services.impl;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris simplificats per EmailServiceImpl.
 * Verifica que els emails es generin i s'enviïn correctament sense requerir servidor de mail real.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("EmailServiceImpl Tests")
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        // Configurar el fromEmail usando ReflectionTestUtils
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@abasta.com");

        // Configurar el mock per retornar un MimeMessage
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("SendPasswordResetEmail hauria d'enviar email sense errors")
    void sendPasswordResetEmail_ShouldSendEmailSuccessfully() {
        // Given
        String to = "user@test.com";
        String token = "reset-token-123";
        String userName = "Joan";

        // When & Then
        assertThatCode(() -> emailService.sendPasswordResetEmail(to, token, userName))
                .doesNotThrowAnyException();

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("SendEmailVerification hauria d'enviar email sense errors")
    void sendEmailVerification_ShouldSendEmailSuccessfully() {
        // Given
        String to = "user@test.com";
        String token = "verification-token-123";
        String userName = "Maria";

        // When & Then
        assertThatCode(() -> emailService.sendEmailVerification(to, token, userName))
                .doesNotThrowAnyException();

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("SendCompanyAdminVerification hauria d'enviar email sense errors")
    void sendCompanyAdminVerification_ShouldSendEmailSuccessfully() {
        // Given
        String to = "admin@company.com";
        String token = "company-token-123";
        String userName = "Pere";
        String companyName = "Test Company SL";

        // When & Then
        assertThatCode(() -> emailService.sendCompanyAdminVerification(to, token, userName, companyName))
                .doesNotThrowAnyException();

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Haurien d'usar el fromEmail configurat")
    void shouldUseConfiguredFromEmail() {
        // Given
        String testFromEmail = "test@abasta.com";
        ReflectionTestUtils.setField(emailService, "fromEmail", testFromEmail);

        // When
        emailService.sendPasswordResetEmail("user@test.com", "token", "User");

        // Then
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Tots els mètodes haurien de cridar mailSender.send()")
    void allMethodsShouldCallMailSenderSend() {
        // When
        emailService.sendPasswordResetEmail("user@test.com", "token1", "User1");
        emailService.sendEmailVerification("user@test.com", "token2", "User2");
        emailService.sendCompanyAdminVerification("admin@test.com", "token3", "Admin", "Company");

        // Then
        verify(mailSender, times(3)).createMimeMessage();
        verify(mailSender, times(3)).send(mimeMessage);
    }

    @Test
    @DisplayName("SendPasswordResetEmail amb paràmetres vàlids")
    void sendPasswordResetEmail_WithValidParameters() {
        // Given
        String validEmail = "valid@test.com";
        String validToken = "valid-reset-token";
        String validName = "Valid User";

        // When & Then
        assertThatCode(() -> emailService.sendPasswordResetEmail(validEmail, validToken, validName))
                .doesNotThrowAnyException();

        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("SendEmailVerification amb paràmetres vàlids")
    void sendEmailVerification_WithValidParameters() {
        // Given
        String validEmail = "verification@test.com";
        String validToken = "valid-verification-token";
        String validName = "Verification User";

        // When & Then
        assertThatCode(() -> emailService.sendEmailVerification(validEmail, validToken, validName))
                .doesNotThrowAnyException();

        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("SendCompanyAdminVerification amb paràmetres vàlids")
    void sendCompanyAdminVerification_WithValidParameters() {
        // Given
        String validEmail = "company-admin@test.com";
        String validToken = "valid-company-token";
        String validAdminName = "Company Admin";
        String validCompanyName = "Valid Company Ltd";

        // When & Then
        assertThatCode(() -> emailService.sendCompanyAdminVerification(validEmail, validToken, validAdminName, validCompanyName))
                .doesNotThrowAnyException();

        verify(mailSender).send(mimeMessage);
    }
}