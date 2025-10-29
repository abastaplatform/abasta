package cat.abasta_back_end.dto;

import cat.abasta_back_end.entities.Company;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CompanyResponseDTOTest {

    @Test
    void whenUsingAllArgsConstructor_thenAllFieldsAreSet() {
        LocalDateTime now = LocalDateTime.now();
        CompanyResponseDTO dto = new CompanyResponseDTO(
                "uuid-123",
                "Empresa X",
                "B12345678",
                "contact@empresa.com",
                "123456789",
                "C/ Exemple, 10",
                "Barcelona",
                "08001",
                Company.CompanyStatus.ACTIVE,
                now,
                now
        );

        assertEquals("uuid-123", dto.getUuid());
        assertEquals("Empresa X", dto.getName());
        assertEquals("B12345678", dto.getTaxId());
        assertEquals("contact@empresa.com", dto.getEmail());
        assertEquals("123456789", dto.getPhone());
        assertEquals("C/ Exemple, 10", dto.getAddress());
        assertEquals("Barcelona", dto.getCity());
        assertEquals("08001", dto.getPostalCode());
        assertEquals(Company.CompanyStatus.ACTIVE, dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void whenUsingBuilder_thenAllFieldsAreSetCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        CompanyResponseDTO dto = CompanyResponseDTO.builder()
                .uuid("uuid-456")
                .name("Empresa Y")
                .taxId("C98765432")
                .email("info@empresa.com")
                .phone("987654321")
                .address("C/ Demo, 5")
                .city("Madrid")
                .postalCode("28001")
                .status(Company.CompanyStatus.INACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals("uuid-456", dto.getUuid());
        assertEquals("Empresa Y", dto.getName());
        assertEquals("C98765432", dto.getTaxId());
        assertEquals("info@empresa.com", dto.getEmail());
        assertEquals("987654321", dto.getPhone());
        assertEquals("C/ Demo, 5", dto.getAddress());
        assertEquals("Madrid", dto.getCity());
        assertEquals("28001", dto.getPostalCode());
        assertEquals(Company.CompanyStatus.INACTIVE, dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void whenMappingFromEntity_thenDtoMatches() {
        LocalDateTime now = LocalDateTime.now();
        Company company = new Company();
        company.setUuid("uuid-789");
        company.setName("Empresa Z");
        company.setTaxId("D11111111");
        company.setEmail("hola@empresa.com");
        company.setPhone("111222333");
        company.setAddress("C/ Test, 1");
        company.setCity("Valencia");
        company.setPostalCode("46001");
        company.setStatus(Company.CompanyStatus.PENDING);
        company.setCreatedAt(now);
        company.setUpdatedAt(now);

        CompanyResponseDTO dto = CompanyResponseDTO.builder()
                .uuid(company.getUuid())
                .name(company.getName())
                .taxId(company.getTaxId())
                .email(company.getEmail())
                .phone(company.getPhone())
                .address(company.getAddress())
                .city(company.getCity())
                .postalCode(company.getPostalCode())
                .status(company.getStatus())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();

        assertEquals(company.getUuid(), dto.getUuid());
        assertEquals(company.getName(), dto.getName());
        assertEquals(company.getStatus(), dto.getStatus());
        assertEquals(company.getCreatedAt(), dto.getCreatedAt());
    }
}
