package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositori JPA per a la gestió de l'accés a dades d'usuaris.
 * Proporciona mètodes per a les operacions CRUD i consultes avançades sobre l'entitat User.
 *
 * <p>Aquest repositori estén JpaRepository per obtenir automàticament les operacions
 * bàsiques de base de dades i defineix mètodes personalitzats per a consultes específiques
 * del domini d'usuaris.</p>
 *
 * <p>Les funcionalitats proporcionades inclouen:
 * <ul>
 *   <li>Operacions CRUD bàsiques heretades de JpaRepository</li>
 *   <li>Cerques per UUID, email i estat actiu</li>
 *   <li>Consultes amb paginació per optimitzar el rendiment</li>
 *   <li>Validacions de tokens de verificació i restabliment de contrasenya</li>
 *   <li>Consultes personalitzades amb @Query per cerques complexes</li>
 *   <li>Filtratge automàtic d'usuaris eliminats (soft delete)</li>
 * </ul>
 * </p>
 *
 * <p>Els mètodes de cerca utilitzen nomenclatura estàndard de Spring Data JPA:
 * <ul>
 *   <li>findBy*: per cerques que retornen entitats</li>
 *   <li>existsBy*: per validacions d'existència</li>
 *   <li>*IgnoreCase: per cerques insensibles a majúscules/minúscules</li>
 *   <li>*Containing: per cerques de text parcial</li>
 * </ul>
 * </p>
 *
 * <p>Exemple d'ús en un servei:
 * <pre>
 * {@literal @}Autowired
 * private UserRepository userRepository;
 *
 * // Cercar usuaris actius d'una empresa
 * Page&lt;User&gt; activeUsers = userRepository
 *     .findByCompanyUuidAndIsDeletedFalse(companyUuid, pageable);
 *
 * // Cercar amb filtres múltiples i paginació
 * Page&lt;User&gt; users = userRepository
 *     .findByCompanyIdAndCriteriaActive(companyId, "john", "doe", null, null, pageable);
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 2.0
 * @since 2025
 * @see User
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Cerca un usuari pel seu email.
     *
     * @param email l'adreça de correu electrònic de l'usuari
     * @return un Optional que conté l'usuari si existeix, o Optional.empty() si no es troba
     */
    Optional<User> findByEmail(String email);

    /**
     * Cerca un usuari pel seu uuid.
     *
     * @param uuid de l'usuari
     * @return un Optional que conté l'usuari si existeix, o Optional.empty() si no es troba
     */
    Optional<User> findByUuid(String uuid);

    /**
     * Cerca un usuari amb un token de verificació d'email vàlid que no hagi expirat.
     * Utilitza una consulta JPQL per verificar que el token coincideix i que la data d'expiració
     * és posterior a la data actual proporcionada.
     *
     * @param token el token de verificació d'email a validar
     * @param now   la data i hora actual per comprovar si el token ha expirat
     * @return un Optional que conté l'usuari si el token és vàlid, o Optional.empty() en cas contrari
     */
    @Query("SELECT u FROM User u WHERE u.emailVerificationToken = :token AND u.emailVerificationExpires > :now")
    Optional<User> findByValidVerificationToken(@Param("token") String token, @Param("now") LocalDateTime now);

    /**
     * Cerca un usuari amb un token de restabliment de contrasenya vàlid que no hagi expirat.
     * Utilitza una consulta JPQL per verificar que el token coincideix i que la data d'expiració
     * és posterior a la data i hora actual proporcionada.
     *
     * @param token el token de restabliment de contrasenya a validar
     * @param now   la data i hora actual per comprovar si el token ha expirat
     * @return un Optional que conté l'usuari si el token és vàlid, o Optional.empty() en cas contrari
     */
    @Query("SELECT u FROM User u WHERE u.passwordResetToken = :token AND u.passwordResetExpires > :now")
    Optional<User> findByValidResetToken(@Param("token") String token, @Param("now") LocalDateTime now);


    /**
     * Verifica si existeix un usuari amb l'email especificat.
     *
     * @param email l'adreça de correu electrònic a comprovar
     * @return true si existeix un usuari amb aquest email, false en cas contrari
     */
    boolean existsByEmail(String email);

    /**
     * Obté tots els usuaris no eliminats d'una empresa amb paginació.
     *
     * <p>Aquest mètode utilitza les convencions de nomenclatura de Spring Data JPA
     * per generar automàticament la query SQL corresponent. Filtra únicament els
     * usuaris que no han estat marcats com a eliminats ({@code isDeleted = false}),
     * incloent tant usuaris actius com inactius.</p>
     *
     * <p>La query generada automàticament és equivalent a:
     * <pre>
     * SELECT u FROM User u
     * WHERE u.company.uuid = :companyUuid
     *   AND u.isDeleted = false
     * ORDER BY [ordenació especificada al Pageable]
     * </pre>
     * </p>
     *
     * <p>Aquest mètode és especialment útil per:
     * <ul>
     *   <li>Llistar tots els usuaris d'una empresa (incloent inactius)</li>
     *   <li>Implementar funcionalitat de soft delete</li>
     *   <li>Mantenir la integritat referencial mentre s'oculten dades "eliminades"</li>
     *   <li>Operacions d'administració que necessiten veure usuaris actius i inactius</li>
     * </ul>
     * </p>
     *
     * @param companyUuid l'identificador UUID únic de l'empresa. No pot ser {@code null}
     * @param pageable informació de paginació i ordenació. No pot ser {@code null}
     * @return una {@link Page} amb els usuaris no eliminats de l'empresa especificada
     * @see Pageable
     * @see Page
     */
    Page<User> findByCompanyUuidAndIsDeletedFalse(String companyUuid, Pageable pageable);

    /**
     * Cerca bàsica d'usuaris d'una empresa en múltiples camps de text amb paginació.
     * Cerca en: email, firstName, lastName i phone de forma simultània.
     * Filtra només usuaris no eliminats (inclou tant actius com inactius).
     *
     * <p>Aquesta consulta implementa una cerca flexible que retorna usuaris si el text
     * de cerca coincideix parcialment amb qualsevol dels camps especificats. La cerca
     * és insensible a majúscules i minúscules.</p>
     *
     * <p>Condicions de filtratge:
     * <ul>
     *   <li>{@code isDeleted = false}: només usuaris no eliminats</li>
     *   <li>Inclou usuaris actius i inactius</li>
     *   <li>Cerca parcial i insensible a majúscules en email, firstName, lastName, phone</li>
     * </ul>
     * </p>
     *
     * @param companyId l'identificador de l'empresa
     * @param searchText el text a cercar (pot ser null per obtenir tots els usuaris)
     * @param pageable informació de paginació
     * @return pàgina d'usuaris que compleixen els criteris
     */
    @Query("SELECT u FROM User u WHERE u.company.id = :companyId AND u.isDeleted = false AND " +
            "(:searchText IS NULL OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.phone) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<User> findByCompanyIdAndMultipleFieldsContainingNoDeleted(
            @Param("companyId") Long companyId,
            @Param("searchText") String searchText,
            Pageable pageable);

    /**
     * Cerca avançada d'usuaris amb múltiples filtres.
     * Inclou tots els camps disponibles per a una cerca completa.
     * Filtra només usuaris no eliminats i permet filtrar per estat actiu/inactiu.
     *
     * <p>Aquesta consulta permet filtrar usuaris per diversos criteris de forma simultània.
     * Tots els paràmetres són opcionals (poden ser {@code null}), permetent combinacions
     * flexibles de filtres segons les necessitats de cerca.</p>
     *
     * <p>Comportament dels filtres:
     * <ul>
     *   <li>Si un paràmetre és {@code null}, no s'aplica aquest filtre</li>
     *   <li>Si un paràmetre té valor, s'aplica una cerca parcial insensible a majúscules (text)</li>
     *   <li>Tots els filtres s'apliquen amb operador AND (han de complir-se tots)</li>
     *   <li>Sempre filtra: {@code isDeleted = false}</li>
     *   <li>isActive: null = tots, true = només actius, false = només inactius</li>
     * </ul>
     * </p>
     *
     * <p>Camps de cerca disponibles:
     * <ul>
     *   <li><strong>email</strong>: cerca parcial en l'adreça de correu electrònic</li>
     *   <li><strong>firstName</strong>: cerca parcial en el nom de l'usuari</li>
     *   <li><strong>lastName</strong>: cerca parcial en els cognoms de l'usuari</li>
     *   <li><strong>phone</strong>: cerca parcial en el número de telèfon</li>
     *   <li><strong>isActive</strong>: filtre d'estat actiu (null, true o false)</li>
     *   <li><strong>emailVerified</strong>: filtre d'email verificat (null, true o false)</li>
     *   <li><strong>role</strong>: filtre de rol (null, ADMIN o USER)</li>
     * </ul>
     * </p>
     *
     * <p>Exemples d'ús:
     * <pre>
     * // Cercar usuaris actius amb email que contingui "john"
     * Page&lt;User&gt; users = repository.findByCompanyIdAndCriteriaActive(
     *     companyId, "john", null, null, null, true, pageable);
     *
     * // Cercar usuaris inactius només per telèfon
     * Page&lt;User&gt; users = repository.findByCompanyIdAndCriteriaActive(
     *     companyId, null, null, null, "555", false, pageable);
     *
     * // Obtenir tots els usuaris (tots els filtres null)
     * Page&lt;User&gt; users = repository.findByCompanyIdAndCriteriaActive(
     *     companyId, null, null, null, null, null, pageable);
     * </pre>
     * </p>
     *
     * @param companyId l'identificador de l'empresa (obligatori, no pot ser {@code null})
     * @param email l'email a cercar (opcional, cerca parcial)
     * @param firstName el nom a cercar (opcional, cerca parcial)
     * @param lastName els cognoms a cercar (opcional, cerca parcial)
     * @param phone el telèfon a cercar (opcional, cerca parcial)
     * @param isActive l'estat actiu a filtrar (null = tots, true = actius, false = inactius)
     * @param emailVerified l'estat de verificació d'email (null = tots, true = verificats, false = no verificats)
     * @param role el rol a filtrar (null = tots, ADMIN o USER)
     * @param pageable informació de paginació i ordenació
     * @return pàgina d'usuaris que compleixen els criteris especificats
     * @see Pageable
     * @see Page
     */
    @Query("SELECT u FROM User u WHERE u.company.id = :companyId AND u.isDeleted = false AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:phone IS NULL OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) AND " +
            "(:isActive IS NULL OR u.isActive = :isActive) AND " +
            "(:emailVerified IS NULL OR u.emailVerified = :emailVerified) AND " +
            "(:role IS NULL OR u.role = :role)")
    Page<User> findByCompanyIdAndCriteriaActive(
            @Param("companyId") Long companyId,
            @Param("email") String email,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("phone") String phone,
            @Param("isActive") Boolean isActive,
            @Param("emailVerified") Boolean emailVerified,
            @Param("role") User.UserRole role,
            Pageable pageable);
}