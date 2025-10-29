package cat.abasta_back_end.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CompanyTest {

    @Test
    void whenBuildCompany_thenFieldsAreSet() {
        LocalDateTime now = LocalDateTime.now();

        Company company = Company.builder()
                .name("Abasta Tech SL")
                .taxId("B12345678")
                .email("info@abasta.com")
                .phone("+34612345678")
                .address("Carrer de l'Exemple, 1")
                .city("Barcelona")
                .postalCode("08001")
                .status(Company.CompanyStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals("Abasta Tech SL", company.getName());
        assertEquals("B12345678", company.getTaxId());
        assertEquals(Company.CompanyStatus.ACTIVE, company.getStatus());
        assertEquals(now, company.getCreatedAt());
        assertEquals(now, company.getUpdatedAt());
    }

    @Test
    void whenCallOnCreate_thenUuidAndDatesAreSet() {
        Company company = new Company();
        assertNull(company.getUuid());
        assertNull(company.getCreatedAt());
        assertNull(company.getUpdatedAt());

        company.onCreate();

        assertNotNull(company.getUuid());
        assertNotNull(company.getCreatedAt());
        assertNotNull(company.getUpdatedAt());
    }

    @Test
    void whenCallOnUpdate_thenUpdatedAtIsChanged() throws InterruptedException {
        Company company = new Company();
        company.onCreate();
        LocalDateTime createdAt = company.getCreatedAt();
        LocalDateTime firstUpdate = company.getUpdatedAt();

        Thread.sleep(10); // Petita espera per assegurar canvi de temps
        company.onUpdate();

        assertEquals(createdAt, company.getCreatedAt());
        assertTrue(company.getUpdatedAt().isAfter(firstUpdate));
    }

    @Test
    void defaultStatusShouldBePending() {
        Company company = new Company();
        company.onCreate();
        assertEquals(Company.CompanyStatus.PENDING, company.getStatus());
    }
}
