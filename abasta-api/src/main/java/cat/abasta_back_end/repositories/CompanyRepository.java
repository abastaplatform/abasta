package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositori per operacions d'accés a dades de l'empresa.
 * Proporciona mètodes estàndards de JPA i consultes personalitzades per la gestió de l'empresa a la plataforma
 *
 * @author Dani Garcia
 * @version 1.0
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Cerca una empresa per el seu identificador UUID.
     *
     * @param uuid Identificador UUID de l'empresa
     * @return Optional amb l'empresa si existeix, buit en cas contrari
     */
    Optional<Company> findByUuid(String uuid);

    /**
     * Verifica si existeix una empresa amb el taxId especificat
     * Útil per validacions de duplicitat abans de crear l'empresa.
     *
     * @param taxId NIF/CIF a verificar
     * @return true si existeix una empresa amb aquest taxId, false en cas contrari
     */
    boolean existsByTaxId(String taxId);

}