package cat.abasta_back_end.exceptions;

/**
 * Excepció personalitzada per indicar que la petició del client és invàlida.
 * <p>
 * Aquesta excepció s'utilitza quan el servidor rep dades incorrectes,
 * incompletes o amb un format inesperat dins d'una sol·licitud HTTP.
 * </p>
 *
 * <p>Exemple d'ús:</p>
 * <pre>
 * if (!user.getIsActive()) {
 *             throw new BadRequestException("Usuari inactiu");
 * }
 * </pre>
 *
 * @author Enrique Pérez
 * @since 1.0
 */
public class BadRequestException extends RuntimeException {
    /**
     * Crea una nova excepció amb el missatge especificat.
     *
     * @param message Descripció de l'error per mostrar al client o als logs.
     */
    public BadRequestException(String message) {
        super(message);
    }
}
