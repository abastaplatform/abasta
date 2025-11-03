package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.SupplierRequestDTO;
import cat.abasta_back_end.dto.SupplierResponseDTO;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.security.JwtUtil;
import cat.abasta_back_end.services.SupplierService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaris per SupplierController.
 * Verifica els endpoints REST per gestió de proveïdors.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@WebMvcTest(SupplierController.class)
@ActiveProfiles("test")
@DisplayName("SupplierController Tests")
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupplierService supplierService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private SupplierRequestDTO supplierRequestDTO;
    private SupplierResponseDTO supplierResponseDTO;

    @BeforeEach
    void setUp() {
        supplierRequestDTO = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdors Catalunya SL")
                .contactName("Joan Martínez")
                .email("joan@provcat.com")
                .phone("938765432")
                .address("Av. Diagonal 123, Barcelona")
                .isActive(true)
                .build();

        supplierResponseDTO = SupplierResponseDTO.builder()
                .uuid("supplier-uuid-123")
                .companyUuid("company-uuid-123")
                .name("Proveïdors Catalunya SL")
                .contactName("Joan Martínez")
                .email("joan@provcat.com")
                .phone("938765432")
                .address("Av. Diagonal 123, Barcelona")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /api/suppliers hauria de crear proveïdor correctament")
    @WithMockUser
    void createSupplier_ShouldReturnCreatedSupplier_WhenValidData() throws Exception {
        // Given
        when(supplierService.createSupplier(any(SupplierRequestDTO.class)))
                .thenReturn(supplierResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/suppliers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Proveïdor creat correctament"))
                .andExpect(jsonPath("$.data.uuid").value("supplier-uuid-123"))
                .andExpect(jsonPath("$.data.name").value("Proveïdors Catalunya SL"))
                .andExpect(jsonPath("$.data.email").value("joan@provcat.com"))
                .andExpect(jsonPath("$.data.isActive").value(true));
    }

    @Test
    @DisplayName("POST /api/suppliers hauria de retornar 400 amb dades invàlides")
    @WithMockUser
    void createSupplier_ShouldReturn400_WhenInvalidData() throws Exception {
        // Given
        SupplierRequestDTO invalidDTO = SupplierRequestDTO.builder()
                .companyUuid("") // UUID buit (invàlid)
                .name("") // Nom buit (invàlid)
                .email("invalid-email") // Email invàlid
                .build();

        // When & Then
        mockMvc.perform(post("/api/suppliers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/suppliers sense autenticació hauria de retornar 401")
    void createSupplier_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/suppliers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierRequestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/suppliers sense CSRF token hauria de retornar 403")
    @WithMockUser
    void createSupplier_ShouldReturn403_WhenNoCsrfToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/suppliers")
                        // No afegim .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierRequestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/suppliers hauria de gestionar errors del servei")
    @WithMockUser
    void createSupplier_ShouldHandle500_WhenServiceThrowsException() throws Exception {
        // Given
        when(supplierService.createSupplier(any(SupplierRequestDTO.class)))
                .thenThrow(new RuntimeException("Error del servei"));

        // When & Then
        mockMvc.perform(post("/api/suppliers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierRequestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/suppliers hauria de gestionar empresa no trobada")
    @WithMockUser
    void createSupplier_ShouldHandle404_WhenCompanyNotFound() throws Exception {
        // Given
        when(supplierService.createSupplier(any(SupplierRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Empresa no trobada"));

        // When & Then
        mockMvc.perform(post("/api/suppliers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Empresa no trobada"));
    }

    @Test
    @DisplayName("POST /api/suppliers hauria d'acceptar proveïdor inactiu")
    @WithMockUser
    void createSupplier_ShouldCreateInactiveSupplier() throws Exception {
        // Given
        SupplierRequestDTO inactiveSupplierDTO = SupplierRequestDTO.builder()
                .companyUuid("company-uuid-123")
                .name("Proveïdor Inactiu SL")
                .contactName("Maria Garcia")
                .email("maria@inactiu.com")
                .phone("987654321")
                .address("Carrer Inactiu 456")
                .isActive(false) // Proveïdor inactiu
                .build();

        SupplierResponseDTO inactiveResponse = SupplierResponseDTO.builder()
                .uuid("supplier-uuid-456")
                .companyUuid("company-uuid-123")
                .name("Proveïdor Inactiu SL")
                .contactName("Maria Garcia")
                .email("maria@inactiu.com")
                .phone("987654321")
                .address("Carrer Inactiu 456")
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(supplierService.createSupplier(any(SupplierRequestDTO.class)))
                .thenReturn(inactiveResponse);

        // When & Then
        mockMvc.perform(post("/api/suppliers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inactiveSupplierDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isActive").value(false))
                .andExpect(jsonPath("$.data.name").value("Proveïdor Inactiu SL"));
    }
}