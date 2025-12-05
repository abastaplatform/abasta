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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // ==================== Tests de findByUuid ====================

    @Test
    @DisplayName("Hauria de trobar un usuari per UUID quan existeix")
    void findByUuid_ShouldReturnUser_WhenUserExists() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser);
        String uuid = savedUser.getUuid();

        // When
        Optional<User> foundUser = userRepository.findByUuid(uuid);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("joan@test.com");
        assertThat(foundUser.get().getFirstName()).isEqualTo("Joan");
    }

    @Test
    @DisplayName("Hauria de retornar Optional buit quan l'UUID no existeix")
    void findByUuid_ShouldReturnEmpty_WhenUuidDoesNotExist() {
        // When
        Optional<User> foundUser = userRepository.findByUuid("uuid-inexistent");

        // Then
        assertThat(foundUser).isEmpty();
    }

    // ==================== Tests de findByCompanyUuidAndIsDeletedFalse ====================

    @Test
    @DisplayName("Hauria de retornar usuaris no eliminats d'una empresa paginats")
    void findByCompanyUuidAndIsDeletedFalse_ShouldReturnNonDeletedUsers() {
        // Given
        User user1 = createUser("user1@test.com", "Maria", "López", false, false);
        User user2 = createUser("user2@test.com", "Pere", "Martínez", false, false);
        User user3 = createUser("user3@test.com", "Anna", "Sánchez", true, false); // Eliminat

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyUuidAndIsDeletedFalse(
                testCompany.getUuid(), pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("user1@test.com", "user2@test.com");
    }

    @Test
    @DisplayName("Hauria de retornar usuaris actius i inactius però no eliminats")
    void findByCompanyUuidAndIsDeletedFalse_ShouldReturnActiveAndInactiveUsers() {
        // Given
        User activeUser = createUser("active@test.com", "Joan", "Actiu", false, true);
        User inactiveUser = createUser("inactive@test.com", "Joan", "Inactiu", false, false);
        User deletedUser = createUser("deleted@test.com", "Joan", "Eliminat", true, true);

        entityManager.persistAndFlush(activeUser);
        entityManager.persistAndFlush(inactiveUser);
        entityManager.persistAndFlush(deletedUser);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyUuidAndIsDeletedFalse(
                testCompany.getUuid(), pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("active@test.com", "inactive@test.com");
    }

    @Test
    @DisplayName("Hauria de respectar la paginació en la cerca d'usuaris")
    void findByCompanyUuidAndIsDeletedFalse_ShouldRespectPagination() {
        // Given
        for (int i = 0; i < 5; i++) {
            User user = createUser("user" + i + "@test.com", "User" + i, "Test", false, true);
            entityManager.persistAndFlush(user);
        }

        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<User> result = userRepository.findByCompanyUuidAndIsDeletedFalse(
                testCompany.getUuid(), pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getNumberOfElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(3);
    }

    // ==================== Tests de findByCompanyIdAndMultipleFieldsContainingNoDeleted ====================

    @Test
    @DisplayName("Hauria de cercar usuaris per email en múltiples camps")
    void findByMultipleFields_ShouldFindByEmail() {
        // Given
        User user1 = createUser("john@test.com", "Maria", "López", false, true);
        User user2 = createUser("maria@test.com", "Joan", "Garcia", false, true);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndMultipleFieldsContainingNoDeleted(
                testCompany.getId(), "john", pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("john@test.com");
    }

    @Test
    @DisplayName("Hauria de cercar usuaris per firstName en múltiples camps")
    void findByMultipleFields_ShouldFindByFirstName() {
        // Given
        User user1 = createUser("user1@test.com", "Joan", "López", false, true);
        User user2 = createUser("user2@test.com", "Maria", "Garcia", false, true);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndMultipleFieldsContainingNoDeleted(
                testCompany.getId(), "Joan", pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getFirstName()).isEqualTo("Joan");
    }

    @Test
    @DisplayName("Hauria de cercar usuaris per lastName en múltiples camps")
    void findByMultipleFields_ShouldFindByLastName() {
        // Given
        User user1 = createUser("user1@test.com", "Joan", "García", false, true);
        User user2 = createUser("user2@test.com", "Maria", "López", false, true);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndMultipleFieldsContainingNoDeleted(
                testCompany.getId(), "García", pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getLastName()).isEqualTo("García");
    }

    @Test
    @DisplayName("Hauria de cercar usuaris per phone en múltiples camps")
    void findByMultipleFields_ShouldFindByPhone() {
        // Given
        User user1 = createUserWithPhone("user1@test.com", "Joan", "García", "600111222");
        User user2 = createUserWithPhone("user2@test.com", "Maria", "López", "600333444");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndMultipleFieldsContainingNoDeleted(
                testCompany.getId(), "600111", pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getPhone()).isEqualTo("600111222");
    }

    @Test
    @DisplayName("Hauria de retornar tots els usuaris quan searchText és null")
    void findByMultipleFields_ShouldReturnAllUsers_WhenSearchTextIsNull() {
        // Given
        User user1 = createUser("user1@test.com", "Joan", "García", false, true);
        User user2 = createUser("user2@test.com", "Maria", "López", false, true);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndMultipleFieldsContainingNoDeleted(
                testCompany.getId(), null, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Hauria d'excloure usuaris eliminats en cerca per múltiples camps")
    void findByMultipleFields_ShouldExcludeDeletedUsers() {
        // Given
        User activeUser = createUser("active@test.com", "Joan", "Actiu", false, true);
        User deletedUser = createUser("deleted@test.com", "Joan", "Eliminat", true, true);

        entityManager.persistAndFlush(activeUser);
        entityManager.persistAndFlush(deletedUser);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndMultipleFieldsContainingNoDeleted(
                testCompany.getId(), "Joan", pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("active@test.com");
    }

    // ==================== Tests de findByCompanyIdAndCriteriaActive ====================

    @Test
    @DisplayName("Hauria de filtrar usuaris per email amb filtres avançats")
    void findByCriteria_ShouldFilterByEmail() {
        // Given
        User user1 = createUser("john@test.com", "Joan", "García", false, true);
        User user2 = createUser("maria@test.com", "Maria", "López", false, true);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndCriteriaActive(
                testCompany.getId(), "john", null, null, null, null, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("john@test.com");
    }

    @Test
    @DisplayName("Hauria de filtrar usuaris per firstName amb filtres avançats")
    void findByCriteria_ShouldFilterByFirstName() {
        // Given
        User user1 = createUser("user1@test.com", "Joan", "García", false, true);
        User user2 = createUser("user2@test.com", "Maria", "López", false, true);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndCriteriaActive(
                testCompany.getId(), null, "Joan", null, null, null, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getFirstName()).isEqualTo("Joan");
    }

    @Test
    @DisplayName("Hauria de filtrar usuaris per lastName amb filtres avançats")
    void findByCriteria_ShouldFilterByLastName() {
        // Given
        User user1 = createUser("user1@test.com", "Joan", "García", false, true);
        User user2 = createUser("user2@test.com", "Maria", "López", false, true);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndCriteriaActive(
                testCompany.getId(), null, null, "López", null, null, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getLastName()).isEqualTo("López");
    }

    @Test
    @DisplayName("Hauria de filtrar usuaris per isActive = true")
    void findByCriteria_ShouldFilterByActiveStatus() {
        // Given
        User activeUser = createUser("active@test.com", "Joan", "Actiu", false, true);
        User inactiveUser = createUser("inactive@test.com", "Maria", "Inactiu", false, false);

        entityManager.persistAndFlush(activeUser);
        entityManager.persistAndFlush(inactiveUser);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndCriteriaActive(
                testCompany.getId(), null, null, null, null, true, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("active@test.com");
    }

    @Test
    @DisplayName("Hauria de filtrar usuaris per isActive = false")
    void findByCriteria_ShouldFilterByInactiveStatus() {
        // Given
        User activeUser = createUser("active@test.com", "Joan", "Actiu", false, true);
        User inactiveUser = createUser("inactive@test.com", "Maria", "Inactiu", false, false);

        entityManager.persistAndFlush(activeUser);
        entityManager.persistAndFlush(inactiveUser);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndCriteriaActive(
                testCompany.getId(), null, null, null, null, false, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("inactive@test.com");
    }

    @Test
    @DisplayName("Hauria de combinar múltiples filtres correctament")
    void findByCriteria_ShouldCombineMultipleFilters() {
        // Given
        User user1 = createUser("john@test.com", "Joan", "García", false, true);
        User user2 = createUser("john2@test.com", "Joan", "López", false, true);
        User user3 = createUser("maria@test.com", "Maria", "García", false, true);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndCriteriaActive(
                testCompany.getId(), "john", "Joan", "García", null, true, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("john@test.com");
    }

    @Test
    @DisplayName("Hauria de retornar tots els usuaris quan tots els filtres són null")
    void findByCriteria_ShouldReturnAllUsers_WhenAllFiltersAreNull() {
        // Given
        User user1 = createUser("user1@test.com", "Joan", "García", false, true);
        User user2 = createUser("user2@test.com", "Maria", "López", false, false);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndCriteriaActive(
                testCompany.getId(), null, null, null, null, null, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Hauria d'excloure usuaris eliminats amb filtres avançats")
    void findByCriteria_ShouldExcludeDeletedUsers() {
        // Given
        User activeUser = createUser("active@test.com", "Joan", "García", false, true);
        User deletedUser = createUser("deleted@test.com", "Joan", "García", true, true);

        entityManager.persistAndFlush(activeUser);
        entityManager.persistAndFlush(deletedUser);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCompanyIdAndCriteriaActive(
                testCompany.getId(), null, "Joan", null, null, null, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("active@test.com");
    }

    // ==================== Mètodes auxiliars ====================

    private User createUser(String email, String firstName, String lastName, boolean isDeleted, boolean isActive) {
        return User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password("password123")
                .role(UserRole.USER)
                .company(testCompany)
                .isDeleted(isDeleted)
                .isActive(isActive)
                .emailVerified(true)
                .build();
    }

    private User createUserWithPhone(String email, String firstName, String lastName, String phone) {
        return User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .password("password123")
                .role(UserRole.USER)
                .company(testCompany)
                .isDeleted(false)
                .isActive(true)
                .emailVerified(true)
                .build();
    }
}