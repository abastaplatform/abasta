package cat.abasta_back_end.controllers;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.entities.User.UserRole;
import cat.abasta_back_end.services.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaris per UserController.
 * Verifica els endpoints REST per a la gestió d'usuaris.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("UserController Tests")
class UserControllerTest {

    private MockMvc mockMvc;
    private UserService userService;

    @BeforeAll
    void setup() {
        userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .build();
    }

    // ==================== Tests de GET /api/users ====================

    @Test
    @DisplayName("GET /api/users hauria de retornar tots els usuaris paginats")
    void getAllUsers_ShouldReturnPagedUsers() throws Exception {
        // Given
        UserResponseDTO user1 = createUserResponseDTO("uuid-1", "test1@abasta.com", "Joan", "Garcia");
        UserResponseDTO user2 = createUserResponseDTO("uuid-2", "test2@abasta.com", "Maria", "Lopez");

        List<UserResponseDTO> users = Arrays.asList(user1, user2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponseDTO> pagedUsers = new PageImpl<>(users, pageable, users.size());

        Mockito.when(userService.getAllUsersPaginated(any(Pageable.class)))
                .thenReturn(pagedUsers);

        // When & Then
        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "email")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuaris de l'empresa obtinguts correctament"))
                .andExpect(jsonPath("$.data.content[0].email").value("test1@abasta.com"))
                .andExpect(jsonPath("$.data.content[1].email").value("test2@abasta.com"))
                .andExpect(jsonPath("$.data.pageable.totalElements").value(2));
    }

    @Test
    @DisplayName("GET /api/users hauria de retornar usuaris ordenats descendent")
    void getAllUsers_ShouldReturnUsersDescending() throws Exception {
        // Given
        UserResponseDTO user1 = createUserResponseDTO("uuid-1", "test1@abasta.com", "Joan", "Garcia");
        List<UserResponseDTO> users = Collections.singletonList(user1);
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponseDTO> pagedUsers = new PageImpl<>(users, pageable, users.size());

        Mockito.when(userService.getAllUsersPaginated(any(Pageable.class)))
                .thenReturn(pagedUsers);

        // When & Then
        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "email")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ==================== Tests de GET /api/users/{uuid} ====================

    @Test
    @DisplayName("GET /api/users/{uuid} hauria de retornar l'usuari correcte")
    void getUserByUuid_ShouldReturnUser() throws Exception {
        // Given
        String uuid = "user-uuid-123";
        UserResponseDTO user = createUserResponseDTO(uuid, "test@abasta.com", "Joan", "Garcia");

        Mockito.when(userService.getUserByUuid(uuid))
                .thenReturn(user);

        // When & Then
        mockMvc.perform(get("/api/users/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuari obtingut correctament"))
                .andExpect(jsonPath("$.data.uuid").value(uuid))
                .andExpect(jsonPath("$.data.email").value("test@abasta.com"))
                .andExpect(jsonPath("$.data.firstName").value("Joan"));
    }

    // ==================== Tests de GET /api/users/search ====================

    @Test
    @DisplayName("GET /api/users/search hauria de retornar usuaris que coincideixin amb el text")
    void searchUsersByText_ShouldReturnMatchingUsers() throws Exception {
        // Given
        String searchText = "Joan";
        UserResponseDTO user = createUserResponseDTO("uuid-1", "joan@abasta.com", "Joan", "Garcia");
        List<UserResponseDTO> users = Collections.singletonList(user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponseDTO> pagedUsers = new PageImpl<>(users, pageable, users.size());

        Mockito.when(userService.searchUsersByText(eq(searchText), any(Pageable.class)))
                .thenReturn(pagedUsers);

        // When & Then
        mockMvc.perform(get("/api/users/search")
                        .param("searchText", searchText)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "email")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cerca bàsica d'usuaris completada"))
                .andExpect(jsonPath("$.data.content[0].firstName").value("Joan"))
                .andExpect(jsonPath("$.data.pageable.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/users/search hauria de retornar llista buida si no hi ha coincidències")
    void searchUsersByText_ShouldReturnEmptyWhenNoMatches() throws Exception {
        // Given
        String searchText = "NoExisteix";
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponseDTO> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        Mockito.when(userService.searchUsersByText(eq(searchText), any(Pageable.class)))
                .thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/users/search")
                        .param("searchText", searchText)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pageable.totalElements").value(0));
    }

    // ==================== Tests de GET /api/users/filter ====================

    @Test
    @DisplayName("GET /api/users/filter hauria de retornar usuaris filtrats per email")
    void filterUsers_ShouldReturnFilteredUsersByEmail() throws Exception {
        // Given
        UserResponseDTO user = createUserResponseDTO("uuid-1", "test@abasta.com", "Joan", "Garcia");
        List<UserResponseDTO> users = Collections.singletonList(user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponseDTO> pagedUsers = new PageImpl<>(users, pageable, users.size());

        Mockito.when(userService.searchUsersWithFilters(any(UserFilterDTO.class), any(Pageable.class)))
                .thenReturn(pagedUsers);

        // When & Then
        mockMvc.perform(get("/api/users/filter")
                        .param("email", "test@abasta.com")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "email")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.content[0].email").value("test@abasta.com"))
                .andExpect(jsonPath("$.data.pageable.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/users/filter hauria de filtrar per múltiples camps")
    void filterUsers_ShouldReturnFilteredUsersByMultipleFields() throws Exception {
        // Given
        UserResponseDTO user = createUserResponseDTO("uuid-1", "test@abasta.com", "Joan", "Garcia");
        List<UserResponseDTO> users = Collections.singletonList(user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponseDTO> pagedUsers = new PageImpl<>(users, pageable, users.size());

        Mockito.when(userService.searchUsersWithFilters(any(UserFilterDTO.class), any(Pageable.class)))
                .thenReturn(pagedUsers);

        // When & Then
        mockMvc.perform(get("/api/users/filter")
                        .param("email", "test")
                        .param("firstName", "Joan")
                        .param("lastName", "Garcia")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pageable.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/users/filter hauria de filtrar per estat actiu")
    void filterUsers_ShouldReturnFilteredUsersByActiveStatus() throws Exception {
        // Given
        UserResponseDTO user = createUserResponseDTO("uuid-1", "test@abasta.com", "Joan", "Garcia");
        List<UserResponseDTO> users = Collections.singletonList(user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponseDTO> pagedUsers = new PageImpl<>(users, pageable, users.size());

        Mockito.when(userService.searchUsersWithFilters(any(UserFilterDTO.class), any(Pageable.class)))
                .thenReturn(pagedUsers);

        // When & Then
        mockMvc.perform(get("/api/users/filter")
                        .param("isActive", "true")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pageable.totalElements").value(1));
    }

    // ==================== Tests de POST /api/users ====================

    @Test
    @DisplayName("POST /api/users hauria de crear un nou usuari")
    void registerUser_ShouldCreateNewUser() throws Exception {
        // Given
        UserResponseDTO createdUser = createUserResponseDTO("new-uuid", "nou@abasta.com", "Maria", "Martínez");

        Mockito.when(userService.registerUser(any(UserRegistrationDTO.class)))
                .thenReturn(createdUser);

        String requestBody = """
                {
                    "email": "nou@abasta.com",
                    "password": "Password123!",
                    "firstName": "Maria",
                    "lastName": "Martínez",
                    "phone": "600123456",
                    "role": "USER"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuari registrat correctament"))
                .andExpect(jsonPath("$.data.email").value("nou@abasta.com"))
                .andExpect(jsonPath("$.data.firstName").value("Maria"));
    }

    @Test
    @DisplayName("POST /api/users hauria de validar dades d'entrada")
    void registerUser_ShouldValidateInput() throws Exception {
        // Given - Request body amb dades invàlides (email buit)
        String invalidRequestBody = """
                {
                    "email": "",
                    "password": "Password123!",
                    "firstName": "Maria",
                    "lastName": "Martínez"
                }
                """;

        // When & Then - Espera error de validació
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest());
    }

    // ==================== Tests de PUT /api/users/{uuid} ====================

    @Test
    @DisplayName("PUT /api/users/{uuid} hauria d'actualitzar l'usuari")
    void updateUser_ShouldUpdateUser() throws Exception {
        // Given
        String uuid = "user-uuid-123";
        UserResponseDTO updatedUser = createUserResponseDTO(uuid, "updated@abasta.com", "Joan", "Garcia Updated");

        Mockito.when(userService.updateUser(eq(uuid), any(UserRequestDTO.class)))
                .thenReturn(updatedUser);

        String requestBody = """
                {
                    "email": "updated@abasta.com",
                    "firstName": "Joan",
                    "lastName": "Garcia Updated",
                    "phone": "600999888",
                    "role": "USER",
                    "isActive": true
                }
                """;

        // When & Then
        mockMvc.perform(put("/api/users/{uuid}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuari actualitzat correctament"))
                .andExpect(jsonPath("$.data.email").value("updated@abasta.com"))
                .andExpect(jsonPath("$.data.lastName").value("Garcia Updated"));
    }

    @Test
    @DisplayName("PUT /api/users/{uuid} hauria de validar UUID")
    void updateUser_ShouldValidateUuid() throws Exception {
        // Given - UUID buit
        String requestBody = """
                {
                    "email": "updated@abasta.com",
                    "firstName": "Joan",
                    "lastName": "Garcia"
                }
                """;

        // When & Then - Espera error
        mockMvc.perform(put("/api/users/ ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    // ==================== Tests de PATCH /api/users/{uuid}/status ====================

    @Test
    @DisplayName("PATCH /api/users/{uuid}/status hauria de canviar l'estat a inactiu")
    void changeUserStatus_ShouldChangeStatusToInactive() throws Exception {
        // Given
        String uuid = "user-uuid-123";
        UserResponseDTO updatedUser = createUserResponseDTO(uuid, "test@abasta.com", "Joan", "Garcia");

        Mockito.when(userService.changeUserStatus(uuid, false))
                .thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(patch("/api/users/{uuid}/status", uuid)
                        .param("isActive", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Estat de l'usuari actualitzat correctament"));
    }

    @Test
    @DisplayName("PATCH /api/users/{uuid}/status hauria de canviar l'estat a actiu")
    void changeUserStatus_ShouldChangeStatusToActive() throws Exception {
        // Given
        String uuid = "user-uuid-123";
        UserResponseDTO updatedUser = createUserResponseDTO(uuid, "test@abasta.com", "Joan", "Garcia");

        Mockito.when(userService.changeUserStatus(uuid, true))
                .thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(patch("/api/users/{uuid}/status", uuid)
                        .param("isActive", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Estat de l'usuari actualitzat correctament"));
    }

    // ==================== Tests de PATCH /api/users/{uuid}/change-password ====================

    @Test
    @DisplayName("PATCH /api/users/{uuid}/change-password hauria de canviar la contrasenya")
    void changePassword_ShouldChangePassword() throws Exception {
        // Given
        String uuid = "user-uuid-123";
        Mockito.doNothing().when(userService).changePassword(eq(uuid), any(PasswordChangeDTO.class));

        String requestBody = """
                {
                    "currentPassword": "oldPassword123!",
                    "newPassword": "newPassword456@"
                }
                """;

        // When & Then
        mockMvc.perform(patch("/api/users/{uuid}/change-password", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Contrasenya canviada correctament"));
    }

    @Test
    @DisplayName("PATCH /api/users/{uuid}/change-password hauria de validar dades")
    void changePassword_ShouldValidateInput() throws Exception {
        // Given - Contrasenya actual buida
        String invalidRequestBody = """
                {
                    "currentPassword": "",
                    "newPassword": "newPassword456@"
                }
                """;

        // When & Then
        mockMvc.perform(patch("/api/users/{uuid}/change-password", "user-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest());
    }

    // ==================== Tests de DELETE /api/users/{uuid} ====================

    @Test
    @DisplayName("DELETE /api/users/{uuid} hauria d'eliminar l'usuari")
    void deleteUser_ShouldDeleteUser() throws Exception {
        // Given
        String uuid = "user-uuid-123";
        Mockito.doNothing().when(userService).deleteUser(uuid);

        // When & Then
        mockMvc.perform(delete("/api/users/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuari eliminat correctament"));
    }

    // ==================== Mètodes auxiliars ====================

    private UserResponseDTO createUserResponseDTO(String uuid, String email, String firstName, String lastName) {
        return UserResponseDTO.builder()
                .uuid(uuid)
                .companyUuid("company-uuid")
                .companyName("Test Company")
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(UserRole.USER)
                .phone("600123456")
                .isActive(true)
                .isDeleted(false)
                .emailVerified(true)
                .lastLogin(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}