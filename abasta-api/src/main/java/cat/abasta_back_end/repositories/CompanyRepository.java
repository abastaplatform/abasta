package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.Company.CompanyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de acceso a datos de empresas.
 * Proporciona métodos estándar de JPA y consultas personalizadas
 * para la gestión de empresas en la plataforma.
 *
 * @author Tu equipo
 * @version 1.0
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Busca una empresa por su identificador UUID.
     *
     * @param uuid Identificador UUID de la empresa
     * @return Optional con la empresa si existe, vacío en caso contrario
     */
    Optional<Company> findByUuid(String uuid);

    /**
     * Busca una empresa por su número de identificación fiscal.
     *
     * @param taxId NIF/CIF de la empresa
     * @return Optional con la empresa si existe, vacío en caso contrario
     */
    Optional<Company> findByTaxId(String taxId);

    /**
     * Busca una empresa por su email de contacto.
     *
     * @param email Email de la empresa
     * @return Optional con la empresa si existe, vacío en caso contrario
     */
    Optional<Company> findByEmail(String email);

    /**
     * Obtiene todas las empresas filtradas por estado.
     *
     * @param status Estado de las empresas a buscar (ACTIVE, INACTIVE, PENDING)
     * @return Lista de empresas con el estado especificado
     */
    List<Company> findByStatus(CompanyStatus status);

    /**
     * Verifica si existe una empresa con el taxId especificado.
     * Útil para validaciones de duplicidad antes de crear empresas.
     *
     * @param taxId NIF/CIF a verificar
     * @return true si existe una empresa con ese taxId, false en caso contrario
     */
    boolean existsByTaxId(String taxId);

    /**
     * Verifica si existe una empresa con el email especificado.
     * Útil para validaciones de duplicidad antes de crear empresas.
     *
     * @param email Email a verificar
     * @return true si existe una empresa con ese email, false en caso contrario
     */
    boolean existsByEmail(String email);
}