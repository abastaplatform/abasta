package cat.abasta_back_end.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaris per PasswordChangeDTO.
 * Verifica la construcció i validació de dades de canvi de contrasenya.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DisplayName("PasswordChangeDTO Tests")
class PasswordChangeDTOTest {

    @Test
    @DisplayName("Hauria de construir DTO amb builder correctament")
    void whenBuildPasswordChangeDTO_thenFieldsAreSet() {
        // Given & When
        PasswordChangeDTO dto = PasswordChangeDTO.builder()
                .currentPassword("OldPassword123!")
                .newPassword("NewPassword456@")
                .build();

        // Then
        assertEquals("OldPassword123!", dto.getCurrentPassword());
        assertEquals("NewPassword456@", dto.getNewPassword());
    }

    @Test
    @DisplayName("Hauria de funcionar amb setters i getters")
    void whenSetFields_thenGettersReturnCorrectValues() {
        // Given
        PasswordChangeDTO dto = new PasswordChangeDTO();

        // When
        dto.setCurrentPassword("CurrentPass123!");
        dto.setNewPassword("NewSecurePass456@");

        // Then
        assertEquals("CurrentPass123!", dto.getCurrentPassword());
        assertEquals("NewSecurePass456@", dto.getNewPassword());
    }

    @Test
    @DisplayName("Hauria de permetre construir amb constructor amb tots els arguments")
    void whenUseAllArgsConstructor_thenFieldsAreSet() {
        // Given & When
        PasswordChangeDTO dto = new PasswordChangeDTO("Old123Pass!", "New456Pass@");

        // Then
        assertEquals("Old123Pass!", dto.getCurrentPassword());
        assertEquals("New456Pass@", dto.getNewPassword());
    }

    @Test
    @DisplayName("Hauria de permetre contrasenyes amb requisits de seguretat complerts")
    void whenPasswordsMeetSecurityRequirements_thenShouldBeValid() {
        // Given & When - Contrasenya amb majúscules, minúscules, número i caràcter especial
        PasswordChangeDTO dto = PasswordChangeDTO.builder()
                .currentPassword("Valid1Pass!")
                .newPassword("Secure2Pass@")
                .build();

        // Then
        assertNotNull(dto.getCurrentPassword());
        assertNotNull(dto.getNewPassword());
        assertTrue(dto.getNewPassword().length() >= 8);
    }

    @Test
    @DisplayName("Hauria de permetre contrasenyes amb caràcters especials diversos")
    void whenPasswordsHaveDifferentSpecialChars_thenShouldBeValid() {
        // Given & When
        PasswordChangeDTO dto1 = PasswordChangeDTO.builder()
                .currentPassword("Pass123!")
                .newPassword("Pass456@")
                .build();

        PasswordChangeDTO dto2 = PasswordChangeDTO.builder()
                .currentPassword("Pass123#")
                .newPassword("Pass456$")
                .build();

        PasswordChangeDTO dto3 = PasswordChangeDTO.builder()
                .currentPassword("Pass123%")
                .newPassword("Pass456&")
                .build();

        // Then
        assertNotNull(dto1.getNewPassword());
        assertNotNull(dto2.getNewPassword());
        assertNotNull(dto3.getNewPassword());
    }

    @Test
    @DisplayName("Hauria de permetre contrasenyes llargues")
    void whenPasswordsAreLong_thenShouldBeValid() {
        // Given & When
        PasswordChangeDTO dto = PasswordChangeDTO.builder()
                .currentPassword("ThisIsAVeryLongPassword123!WithLotsOfCharacters")
                .newPassword("AnotherVeryLongPassword456@WithManyCharacters")
                .build();

        // Then
        assertNotNull(dto.getCurrentPassword());
        assertNotNull(dto.getNewPassword());
        assertTrue(dto.getCurrentPassword().length() > 8);
        assertTrue(dto.getNewPassword().length() > 8);
    }

    @Test
    @DisplayName("Hauria de permetre contrasenyes diferents")
    void whenPasswordsAreDifferent_thenShouldBeAllowed() {
        // Given & When
        PasswordChangeDTO dto = PasswordChangeDTO.builder()
                .currentPassword("Current123!")
                .newPassword("Different456@")
                .build();

        // Then
        assertNotEquals(dto.getCurrentPassword(), dto.getNewPassword());
    }

    @Test
    @DisplayName("Hauria de permetre contrasenyes iguals (validació a nivell de servei)")
    void whenPasswordsAreSame_thenShouldBeAllowedAtDTOLevel() {
        // Given & When - El DTO no valida que siguin diferents (ho fa el servei)
        PasswordChangeDTO dto = PasswordChangeDTO.builder()
                .currentPassword("SamePass123!")
                .newPassword("SamePass123!")
                .build();

        // Then - A nivell de DTO és vàlid, la validació de negoci és al servei
        assertEquals(dto.getCurrentPassword(), dto.getNewPassword());
    }

    @Test
    @DisplayName("Hauria de mantenir les contrasenyes exactament com s'introdueixen")
    void whenPasswordsSet_thenShouldPreserveExactValue() {
        // Given
        String currentPwd = "  Exact123!  ";  // Amb espais
        String newPwd = "  NewExact456@  ";

        // When
        PasswordChangeDTO dto = PasswordChangeDTO.builder()
                .currentPassword(currentPwd)
                .newPassword(newPwd)
                .build();

        // Then - No fa trim ni modifica les contrasenyes
        assertEquals(currentPwd, dto.getCurrentPassword());
        assertEquals(newPwd, dto.getNewPassword());
    }

    @Test
    @DisplayName("Hauria de permetre contrasenyes amb combinacions complexes")
    void whenPasswordsHaveComplexCombinations_thenShouldBeValid() {
        // Given & When
        PasswordChangeDTO dto = PasswordChangeDTO.builder()
                .currentPassword("C0mpl3x!P@ssw0rd#2023")
                .newPassword("N3w$Secur3%P@ss&2024")
                .build();

        // Then
        assertNotNull(dto.getCurrentPassword());
        assertNotNull(dto.getNewPassword());
    }
}