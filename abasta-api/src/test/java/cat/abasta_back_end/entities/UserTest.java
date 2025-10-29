package cat.abasta_back_end.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testBuilderICampsPerDefecte() {
        Company company = Company.builder()
                .name("Abasta Tech")
                .email("empresa@abasta.com")
                .taxId("B12345678")
                .build();

        User user = User.builder()
                .email("joan.garcia@example.com")
                .password("Password123!")
                .firstName("Joan")
                .lastName("Garcia")
                .company(company)
                .build();

        // Comprovacions dels camps
        assertThat(user.getEmail()).isEqualTo("joan.garcia@example.com");
        assertThat(user.getFirstName()).isEqualTo("Joan");
        assertThat(user.getLastName()).isEqualTo("Garcia");

        // Comprovació valors per defecte
        assertThat(user.getIsActive()).isTrue();
        assertThat(user.getEmailVerified()).isFalse();
        assertThat(user.getRole()).isEqualTo(User.UserRole.USER);
    }

    @Test
    void testOnCreateGeneraUUIDiDates() {
        Company company = Company.builder()
                .name("Abasta Tech")
                .email("empresa@abasta.com")
                .taxId("B12345678")
                .build();

        User user = User.builder()
                .email("anna@example.com")
                .password("Password123!")
                .firstName("Anna")
                .lastName("Lopez")
                .company(company)
                .build();

        // Simulem @PrePersist
        user.onCreate();

        assertThat(user.getUuid()).isNotNull();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    void testOnUpdateActualitzaUpdatedAt() throws InterruptedException {
        Company company = Company.builder()
                .name("Abasta Tech")
                .email("empresa@abasta.com")
                .taxId("B12345678")
                .build();

        User user = User.builder()
                .email("pau@example.com")
                .password("Password123!")
                .firstName("Pau")
                .lastName("Marti")
                .company(company)
                .build();

        // Inicialitzar dates
        user.onCreate();
        LocalDateTime antigaData = user.getUpdatedAt();

        // Esperar un instant
        Thread.sleep(1000);

        // Simulem @PreUpdate
        user.onUpdate();

        assertThat(user.getUpdatedAt()).isAfter(antigaData);
    }
}
