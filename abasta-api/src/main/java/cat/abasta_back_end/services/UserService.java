package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.exceptions.BadRequestException;
import cat.abasta_back_end.exceptions.DuplicateResourceException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;

/**
 * Interfície de servei per gestionar operacions relacionades amb usuaris.
 * Proporciona funcionalitats per a l'autenticació, restabliment de contrasenyes
 * i verificació d'emails, entre d'altres.
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 */
public interface UserService {

    /**
     * Autentica un usuari a la plataforma mitjançant les seves credencials (correu electrònic i la contrasenya).
     * Si les credencials són correctes, retorna un token o la informació necessària per mantenir la sessió iniciada.
     *
     * @param loginDTO l'objecte que conté les credencials d'inici de sessió (correu electrònic i contrasenya)
     * @return un {@link LoginResponseDTO} amb la informació de l'usuari autenticat i/o el token de sessió
     */
    LoginResponseDTO login(LoginRequestDTO loginDTO);

    /**
     * Inicia el procés de restabliment de contrasenya per a un usuari.
     * Genera un token de restabliment i envia un correu electrònic amb les instruccions.
     *
     * @param email l'adreça de correu electrònic de l'usuari que vol restablir la contrasenya
     * @throws ResourceNotFoundException si no es troba cap usuari amb l'email especificat
     */
    void requestPasswordReset(String email);

    /**
     * Restableix la contrasenya d'un usuari utilitzant un token de restabliment vàlid.
     *
     * @param passwordResetDTO l'objecte que conté el token i la nova contrasenya
     * @throws BadRequestException si el token és invàlid o ha expirat
     */
    void resetPassword(PasswordResetDTO passwordResetDTO);

    /**
     * Verifica l'adreça de correu electrònic d'un usuari utilitzant un token de verificació.
     * Si l'usuari és un administrador d'empresa, també activa l'empresa associada.
     *
     * @param token el token de verificació d'email
     * @throws BadRequestException si el token és invàlid o ha expirat
     */
    void verifyEmail(String token);

    /**
     * Reenvia el correu electrònic de verificació a un usuari.
     * Genera un nou token de verificació abans d'enviar el correu.
     *
     * @param email l'adreça de correu electrònic de l'usuari
     * @throws ResourceNotFoundException si no es troba cap usuari amb l'email especificat
     * @throws BadRequestException si l'email ja està verificat
     */
    void resendVerificationEmail(String email);

    /**
     * Registra un nou usuari al sistema.
     * <p>
     * Aquest mètode crea un nou compte d'usuari associat a l'empresa de l'usuari
     * autenticat.
     * Només els usuaris amb rol d'administrador poden registrar un nou usuari.
     * El procés inclou:
     * </p>
     * <ul>
     *   <li>Validació que el rol sigui administrador</li>
     *   <li>Validació que l'email no estigui duplicat</li>
     *   <li>Verificació de l'existència de l'empresa</li>
     *   <li>Generació d'un token de verificació vàlid per 24 hores</li>
     *   <li>Encriptació de la contrasenya</li>
     *   <li>Enviament d'un correu electrònic de verificació</li>
     * </ul>
     * <p>
     * L'usuari creat romandrà inactiu fins que verifiqui el seu correu electrònic
     * mitjançant el token generat.
     * </p>
     *
     * @param registrationDTO les dades de registre de l'usuari. No pot ser null
     * @return UserResponseDTO amb les dades de l'usuari creat
     * @throws DuplicateResourceException si ja existeix un usuari amb l'email proporcionat
     * @throws ResourceNotFoundException si l'empresa associada no existeix
     * @throws BadRequestException si l'usuari autenticat no té rol d'administrador
     */
    UserResponseDTO registerUser(UserRegistrationDTO registrationDTO);

    /**
     * Obté un usuari pel seu identificador UUID.
     * Només els usuaris amb rol d'administrador poden accedir a aquesta informació.
     *
     * @param uuid identificador únic de l'usuari a cercar
     * @return DTO amb les dades de l'usuari trobat
     * @throws ResourceNotFoundException si no existeix cap usuari amb l'UUID especificat
     * @throws BadRequestException si l'usuari autenticat no té rol d'administrador
     */
    UserResponseDTO getUserByUuid(String uuid);

    /**
     * Actualitza les dades d'un usuari existent.
     * <p>
     * Permet modificar l'email, nom, cognoms, telèfon, rol i estat actiu de l'usuari.
     * Valida que el nou email no estigui ja en ús per un altre usuari.
     * Els camps rol i estat actiu només s'actualitzen si es proporcionen valors no nuls.
     * Només els usuaris amb rol d'administrador poden actualitzar les dades de l'usuari.
     * </p>
     *
     * @param uuid identificador únic de l'usuari a actualitzar
     * @param userRequestDTO DTO amb les noves dades de l'usuari
     * @return DTO amb les dades de l'usuari actualitzat
     * @throws ResourceNotFoundException si no existeix cap usuari amb l'UUID especificat
     * @throws DuplicateResourceException si el nou email ja està registrat per un altre usuari
     * @throws BadRequestException si l'usuari autenticat no té rol d'administrador
     */
    UserResponseDTO updateUser(String uuid, UserRequestDTO userRequestDTO);

    /**
     * Canvia l'estat actiu/inactiu d'un usuari.
     * Només els usuaris amb rol d'administrador poden canviar l'estat.
     *
     * @param uuid identificador únic de l'usuari
     * @param isActive nou estat d'activació ({@code true} per actiu, {@code false} per inactiu)
     * @return DTO amb les dades de l'usuari amb l'estat actualitzat
     * @throws ResourceNotFoundException si no existeix cap usuari amb l'UUID especificat
     * @throws BadRequestException si l'usuari autenticat no té rol d'administrador
     */
    UserResponseDTO changeUserStatus(String uuid, Boolean isActive);

    /**
     * Canvia la contrasenya d'un usuari.
     * <p>
     * Requereix verificació de la contrasenya actual abans de permetre el canvi.
     * La nova contrasenya s'emmagatzema codificada.
     * </p>
     *
     * @param uuid identificador únic de l'usuari
     * @param passwordChangeDTO DTO amb la contrasenya actual i la nova contrasenya
     * @throws ResourceNotFoundException si no existeix cap usuari amb l'UUID especificat
     * @throws BadRequestException si la contrasenya actual proporcionada no és correcta
     */
    void changePassword(String uuid, PasswordChangeDTO passwordChangeDTO);

    /**
     * Elimina un usuari de forma lògica (soft delete).
     * Només els usuaris amb rol d'administrador poden eliminar usuaris.
     *
     * <p>
     * L'usuari no s'elimina físicament de la base de dades,
     * sinó que es marca com a eliminat per mantenir la integritat referencial.
     * </p>
     *
     * @param uuid identificador únic de l'usuari a eliminar
     * @throws ResourceNotFoundException si no existeix cap usuari amb l'UUID especificat
     * @throws BadRequestException si l'usuari autenticat no té rol d'administrador
     */
    void deleteUser(String uuid);
}