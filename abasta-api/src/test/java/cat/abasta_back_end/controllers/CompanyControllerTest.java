package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyRequestDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.entities.Company.CompanyStatus;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.security.JwtUtil;
import cat.abasta_back_end.services.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaris per CompanyController.
 * Verifica els endpoints REST per gestió d'empreses.
 * <p>
 * Actualitzat per usar @MockitoBean en lloc de @MockBean deprecated.
 *
 * @author Enrique Pérez
 * @version 1.1
 */
@WebMvcTest(CompanyController.class)
@ActiveProfiles("test")
@DisplayName("CompanyController Tests")
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private CompanyResponseDTO companyResponse;
    private CompanyRequestDTO updateDTO;

    @BeforeEach
    void setUp() {
        companyResponse = CompanyResponseDTO.builder()
                .uuid("company-uuid-123")
                .name("Test Company SL")
                .taxId("B12345678")
                .email("empresa@test.com")
                .phone("123456789")
                .address("Carrer Test 123")
                .city("Barcelona")
                .postalCode("08001")
                .status(CompanyStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CompanyRegistrationDTO registrationDTO = CompanyRegistrationDTO.builder()
                .companyName("Test Company SL")
                .taxId("B12345678")
                .companyEmail("empresa@test.com")
                .companyPhone("123456789")
                .companyAddress("Carrer Test 123")
                .companyCity("Barcelona")
                .companyPostalCode("08001")
                .adminEmail("admin@test.com")
                .adminPassword("password123")
                .adminFirstName("Joan")
                .adminLastName("Garcia")
                .adminPhone("987654321")
                .build();

        updateDTO = CompanyRequestDTO.builder()
                .name("Updated Company SL")
                .taxId("B12345678") // Afegir taxId requerit
                .email("updated@test.com")
                .phone("987654321")
                .address("Carrer Updated 456")
                .city("Madrid")
                .postalCode("28001")
                .build();
    }

    @Test
    @DisplayName("GET /api/companies/{uuid} hauria de retornar empresa per UUID")
    @WithMockUser
    void getCompanyByUuid_ShouldReturnCompany_WhenUuidExists() throws Exception {
        // Given
        String companyUuid = "company-uuid-123";
        when(companyService.getCompanyByUuid(companyUuid)).thenReturn(companyResponse);

        // When & Then
        mockMvc.perform(get("/api/companies/{uuid}", companyUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Empresa recuperada exitosament"))
                .andExpect(jsonPath("$.data.uuid").value("company-uuid-123"))
                .andExpect(jsonPath("$.data.name").value("Test Company SL"))
                .andExpect(jsonPath("$.data.taxId").value("B12345678"))
                .andExpect(jsonPath("$.data.email").value("empresa@test.com"));
    }

    @Test
    @DisplayName("GET /api/companies/{uuid} hauria de retornar 404 quan UUID no existeix")
    @WithMockUser
    void getCompanyByUuid_ShouldReturn404_WhenUuidNotExists() throws Exception {
        // Given
        String nonExistentUuid = "non-existent-uuid";
        when(companyService.getCompanyByUuid(nonExistentUuid))
                .thenThrow(new ResourceNotFoundException("Empresa no trobada"));

        // When & Then
        mockMvc.perform(get("/api/companies/{uuid}", nonExistentUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Empresa no trobada"));
    }

    @Test
    @DisplayName("PUT /api/companies/{uuid} hauria d'actualitzar empresa correctament")
    @WithMockUser
    void updateCompany_ShouldReturnUpdatedCompany_WhenValidData() throws Exception {
        // Given
        String companyUuid = "company-uuid-123";
        CompanyResponseDTO updatedResponse = CompanyResponseDTO.builder()
                .uuid(companyUuid)
                .name("Updated Company SL")
                .taxId("B12345678")
                .email("updated@test.com")
                .phone("987654321")
                .address("Carrer Updated 456")
                .city("Madrid")
                .postalCode("28001")
                .status(CompanyStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(companyService.updateCompany(eq(companyUuid), any(CompanyRequestDTO.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/companies/{uuid}", companyUuid)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Empresa actualitzada exitosament"))
                .andExpect(jsonPath("$.data.uuid").value(companyUuid))
                .andExpect(jsonPath("$.data.name").value("Updated Company SL"))
                .andExpect(jsonPath("$.data.email").value("updated@test.com"))
                .andExpect(jsonPath("$.data.city").value("Madrid"));
    }

    @Test
    @DisplayName("PUT /api/companies/{uuid} hauria de retornar 404 quan UUID no existeix")
    @WithMockUser
    void updateCompany_ShouldReturn404_WhenUuidNotExists() throws Exception {
        // Given
        String nonExistentUuid = "non-existent-uuid";
        when(companyService.updateCompany(eq(nonExistentUuid), any(CompanyRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Empresa no trobada"));

        // When & Then
        mockMvc.perform(put("/api/companies/{uuid}", nonExistentUuid)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Empresa no trobada"));
    }

    @Test
    @DisplayName("PUT /api/companies/{uuid} hauria de retornar 400 amb dades invàlides")
    @WithMockUser
    void updateCompany_ShouldReturn400_WhenInvalidData() throws Exception {
        // Given
        String companyUuid = "company-uuid-123";
        CompanyRequestDTO invalidDTO = CompanyRequestDTO.builder()
                .name("") // Nom buit (invàlid)
                .taxId("") // TaxId buit (invàlid)
                .email("invalid-email") // Email invàlid
                .build();

        // When & Then
        mockMvc.perform(put("/api/companies/{uuid}", companyUuid)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/companies/{uuid} sense autenticació hauria de retornar 401")
    void getCompanyByUuid_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        // Given
        String companyUuid = "company-uuid-123";

        // When & Then
        mockMvc.perform(get("/api/companies/{uuid}", companyUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/companies/{uuid} sense autenticació hauria de retornar 401")
    void updateCompany_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        // Given
        String companyUuid = "company-uuid-123";

        // When & Then
        mockMvc.perform(put("/api/companies/{uuid}", companyUuid)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isUnauthorized());
    }
}