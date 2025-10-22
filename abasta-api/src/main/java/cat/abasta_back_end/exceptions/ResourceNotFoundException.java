package cat.abasta_back_end.exceptions;

/**
 * Excepció personalitzada per indicar que no s'ha trobat el recurs sol·licitat.
 * <p>
 * Aquesta excepció és útil quan es realitzen cerques per ID o paràmetres
 * i no es troba cap coincidència a la base de dades.
 * </p>
 *
 * <p>Exemple d'ús:</p>
 * <pre>
 * User user = userRepository.findById(id)
 *         .orElseThrow(() -> new ResourceNotFoundException("Usuari no trobat amb ID: " + id));
 * </pre>
 *
 * @autor Enrique Pérez
 * @since 1.0
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Crea una nova excepció amb el missatge especificat.
     *
     * @param message Descripció de l'error per mostrar al client o als logs.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
