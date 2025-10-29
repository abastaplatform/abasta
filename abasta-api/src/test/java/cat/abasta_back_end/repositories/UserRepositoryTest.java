package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.User;
import cat.abasta_back_end.entities.User.UserRole;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.Company.CompanyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaris simplificats per al repositori UserRepository.
 * Utilitza @Sql per configurar l'esquema de manera simple.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/test-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        // Crear empresa de test
        testCompany = Company.builder()
                .name("Empresa Test")
                .taxId("B12345678")
                .email("empresa@test.com")
                .status(CompanyStatus.ACTIVE)
                .build();
        entityManager.persistAndFlush(testCompany);

        // Crear usuari de test
        testUser = User.builder()
                .firstName("Joan")
                .lastName("Garcia")
                .email("joan@test.com")
                .password("password123")
                .role(UserRole.USER)
                .company(testCompany)
                .isActive(true)
                .emailVerified(false)
                .build();
    }

    @Test
    @DisplayName("Hauria de trobar un usuari per email quan existeix")
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> foundUser = userRepository.findByEmail("joan@test.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getFirstName()).isEqualTo("Joan");
        assertThat(foundUser.get().getLastName()).isEqualTo("Garcia");
    }

    @Test
    @DisplayName("Hauria de retornar Optional buit quan l'usuari no existeix")
    void findByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("noexisteix@test.com");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Hauria de verificar si existeix un usuari per email")
    void existsByEmail_ShouldReturnTrue_WhenUserExists() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        boolean exists = userRepository.existsByEmail("joan@test.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Hauria de retornar false quan no existeix usuari amb email especificat")
    void existsByEmail_ShouldReturnFalse_WhenUserDoesNotExist() {
        // When
        boolean exists = userRepository.existsByEmail("noexisteix@test.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Hauria de trobar usuari amb token de verificació vàlid")
    void findByValidVerificationToken_ShouldReturnUser_WhenTokenIsValid() {
        // Given
        String validToken = "valid-token-123";
        LocalDateTime futureExpiry = LocalDateTime.now().plusHours(1);

        testUser.setEmailVerificationToken(validToken);
        testUser.setEmailVerificationExpires(futureExpiry);
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> foundUser = userRepository.findByValidVerificationToken(validToken, LocalDateTime.now());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("joan@test.com");
    }

    @Test
    @DisplayName("Hauria de retornar buit quan el token de verificació ha expirat")
    void findByValidVerificationToken_ShouldReturnEmpty_WhenTokenIsExpired() {
        // Given
        String expiredToken = "expired-token-123";
        LocalDateTime pastExpiry = LocalDateTime.now().minusHours(1);

        testUser.setEmailVerificationToken(expiredToken);
        testUser.setEmailVerificationExpires(pastExpiry);
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> foundUser = userRepository.findByValidVerificationToken(expiredToken, LocalDateTime.now());

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Hauria de trobar usuari amb token de reset vàlid")
    void findByValidResetToken_ShouldReturnUser_WhenTokenIsValid() {
        // Given
        String validResetToken = "reset-token-123";
        LocalDateTime futureExpiry = LocalDateTime.now().plusHours(1);

        testUser.setPasswordResetToken(validResetToken);
        testUser.setPasswordResetExpires(futureExpiry);
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> foundUser = userRepository.findByValidResetToken(validResetToken, LocalDateTime.now());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("joan@test.com");
    }

    @Test
    @DisplayName("Hauria de retornar buit quan el token de reset ha expirat")
    void findByValidResetToken_ShouldReturnEmpty_WhenTokenIsExpired() {
        // Given
        String expiredResetToken = "expired-reset-token-123";
        LocalDateTime pastExpiry = LocalDateTime.now().minusHours(1);

        testUser.setPasswordResetToken(expiredResetToken);
        testUser.setPasswordResetExpires(pastExpiry);
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> foundUser = userRepository.findByValidResetToken(expiredResetToken, LocalDateTime.now());

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Hauria de desar i recuperar un usuari correctament")
    void save_ShouldPersistAndRetrieveUser_Successfully() {
        // When
        User savedUser = userRepository.save(testUser);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getFirstName()).isEqualTo("Joan");
        assertThat(savedUser.getLastName()).isEqualTo("Garcia");
        assertThat(savedUser.getEmail()).isEqualTo("joan@test.com");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
        assertThat(savedUser.getCompany()).isEqualTo(testCompany);
    }

    @Test
    @DisplayName("Hauria de gestionar correctament els diferents rols d'usuari")
    void save_ShouldHandleDifferentUserRoles_Correctly() {
        // Given - Usuaris amb diferents rols
        User adminUser = User.builder()
                .firstName("Maria")
                .lastName("Admin")
                .email("admin@test.com")
                .password("admin123")
                .role(UserRole.ADMIN)
                .company(testCompany)
                .isActive(true)
                .emailVerified(true)
                .build();

        User regularUser = User.builder()
                .firstName("Pere")
                .lastName("User")
                .email("user@test.com")
                .password("user123")
                .role(UserRole.USER)
                .company(testCompany)
                .isActive(true)
                .emailVerified(false)
                .build();

        // When - Desar els usuaris
        User savedAdmin = userRepository.save(adminUser);
        User savedUser = userRepository.save(regularUser);

        // Then - Els rols haurien d'estar configurats correctament
        assertThat(savedAdmin.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
        assertThat(savedAdmin.getEmailVerified()).isTrue();
        assertThat(savedUser.getEmailVerified()).isFalse();
    }
}