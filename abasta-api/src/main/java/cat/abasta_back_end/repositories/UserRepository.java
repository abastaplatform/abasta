package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositori JPA per gestionar les operacions de persistència de l'entitat User.
 * Proporciona mètodes per cercar usuaris per email, validar tokens de restabliment de contrasenya
 * i verificació d'email.
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
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
     * Cerca un usuari amb un token de restabliment de contrasenya vàlid que no hagi expirat.
     * Utilitza una consulta JPQL per verificar que el token coincideix i que la data d'expiració
     * és posterior a la data actual proporcionada.
     *
     * @param token el token de restabliment de contrasenya a validar
     * @param now la data i hora actual per comprovar si el token ha expirat
     * @return un Optional que conté l'usuari si el token és vàlid, o Optional.empty() en cas contrari
     */
    @Query("SELECT u FROM User u WHERE u.passwordResetToken = :token AND u.passwordResetExpires > :now")
    Optional<User> findByValidResetToken(@Param("token") String token, @Param("now") LocalDateTime now);

    /**
     * Cerca un usuari amb un token de verificació d'email vàlid que no hagi expirat.
     * Utilitza una consulta JPQL per verificar que el token coincideix i que la data d'expiració
     * és posterior a la data actual proporcionada.
     *
     * @param token el token de verificació d'email a validar
     * @param now la data i hora actual per comprovar si el token ha expirat
     * @return un Optional que conté l'usuari si el token és vàlid, o Optional.empty() en cas contrari
     */
    @Query("SELECT u FROM User u WHERE u.emailVerificationToken = :token AND u.emailVerificationExpires > :now")
    Optional<User> findByValidVerificationToken(@Param("token") String token, @Param("now") LocalDateTime now);

    /**
     * Verifica si existeix un usuari amb l'email especificat.
     *
     * @param email l'adreça de correu electrònic a comprovar
     * @return true si existeix un usuari amb aquest email, false en cas contrari
     */
    boolean existsByEmail(String email);
}