package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyRequestDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.exceptions.DuplicateResourceException;
import cat.abasta_back_end.services.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests per CompanyController
 */
@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CompanyService companyService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        CompanyController companyController = new CompanyController(companyService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(companyController)
                .setControllerAdvice() // preparado para GlobalExceptionHandler
                .build();
    }

    // ============================================================
    // GET /api/companies
    // ============================================================
    @Test
    @DisplayName("GET /api/companies - Hauria de retornar la informació de l'empresa")
    void getCompany_ShouldReturnCompanyInfo_WhenExists() throws Exception {
        CompanyResponseDTO companyResponse = CompanyResponseDTO.builder()
                .uuid("company-123")
                .name("Test Company")
                .taxId("B12345678")
                .email("test@company.com")
                .phone("123456789")
                .address("Test Address")
                .city("Barcelona")
                .postalCode("08001")
                .status(Company.CompanyStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(companyService.getCompanyByUuid()).thenReturn(companyResponse);

        mockMvc.perform(get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Test Company"));
    }

    // ============================================================
    // PUT /api/companies
    // ============================================================
    @Test
    @DisplayName("PUT /api/companies - Hauria d'actualitzar l'empresa")
    void updateCompany_ShouldUpdateCompany_WhenValidData() throws Exception {

        CompanyRequestDTO request = CompanyRequestDTO.builder()
                .name("Updated Company")
                .taxId("B87654321")
                .email("updated@company.com")
                .phone("987654321")
                .address("Updated Address")
                .city("Madrid")
                .postalCode("28001")
                .build();

        CompanyResponseDTO response = CompanyResponseDTO.builder()
                .uuid("company-123")
                .name("Updated Company")
                .taxId("B87654321")
                .email("updated@company.com")
                .phone("987654321")
                .address("Updated Address")
                .city("Madrid")
                .postalCode("28001")
                .status(Company.CompanyStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(companyService.updateCompany(any(CompanyRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Updated Company"));
    }

    // ============================================================
    // POST /api/companies/register
    // ============================================================
    @Test
    @DisplayName("POST /api/companies/register - Hauria de registrar empresa i administrador")
    void registerCompany_ShouldCreateCompany_WhenValidData() throws Exception {

        CompanyRegistrationDTO request = CompanyRegistrationDTO.builder()
                .companyName("NewCo")
                .taxId("A99999999")
                .companyEmail("info@newco.com")
                .adminEmail("admin@newco.com")
                .adminPassword("Password1!")
                .adminFirstName("Admin")
                .adminLastName("User")
                .build();

        CompanyResponseDTO companyResponse = CompanyResponseDTO.builder()
                .uuid("uuid-123")
                .name("NewCo")
                .taxId("A99999999")
                .build();

        when(companyService.registerCompanyWithAdmin(any(CompanyRegistrationDTO.class)))
                .thenReturn(companyResponse);

        mockMvc.perform(post("/api/companies/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.company.name").value("NewCo"));
    }
}
