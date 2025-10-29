package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.services.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private CompanyController companyController;

    private CompanyRegistrationDTO registrationDTO;
    private CompanyResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(companyController).build();

        registrationDTO = new CompanyRegistrationDTO();
        registrationDTO.setCompanyName("Test Company");
        registrationDTO.setAdminEmail("admin@test.com");
        registrationDTO.setAdminPassword("Admin123!");
        registrationDTO.setAdminFirstName("Dani");
        registrationDTO.setAdminLastName("Garcia");
        registrationDTO.setTaxId("B12345678");

        responseDTO = new CompanyResponseDTO();
        responseDTO.setUuid(String.valueOf(UUID.randomUUID()));
        responseDTO.setName("Test Company");
    }

    @Test
    void testRegisterCompanyWithAdmin() throws Exception {
        Mockito.when(companyService.registerCompanyWithAdmin(registrationDTO))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/companies/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.company.name", is("Test Company")))
                .andExpect(jsonPath("$.data.message", containsString("Si us plau, verifica l'email")));
    }
}
