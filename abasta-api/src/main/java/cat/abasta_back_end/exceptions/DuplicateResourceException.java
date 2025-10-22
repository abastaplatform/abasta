package cat.abasta_back_end.exceptions;

/**
 * Excepció personalitzada per indicar que ja existeix un recurs duplicat.
 * <p>
 * S'utilitza en operacions de registre quan
 * s'intenta inserir un recurs amb un identificador o valor únic
 * que ja està present a la base de dades.
 * </p>
 *
 * <p>Exemple d'ús:</p>
 * <pre>
 * if (userRepository.existsByEmail(email)) {
 *     throw new DuplicateResourceException("Ja existeix un usuari amb aquest correu");
 * }
 * </pre>
 *
 * @author Enrique Pérez
 * @since 1.0
 */
public class DuplicateResourceException extends RuntimeException {
    /**
     * Crea una nova excepció amb el missatge especificat.
     *
     * @param message Descripció de l'error per mostrar al client o als logs.
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}
