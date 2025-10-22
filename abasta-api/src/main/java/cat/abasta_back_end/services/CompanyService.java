package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyRequestDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.entities.Company;

import java.util.List;

/**
 * Servicio para la gestión de empresas en la plataforma.
 * Define las operaciones de negocio relacionadas con la creación, consulta,
 * actualización y eliminación de empresas.
 *
 * @author Tu equipo
 * @version 1.0
 */
public interface CompanyService {

    /**
     * Crea una nueva empresa en la plataforma sin usuario administrador.
     * Utilizado para operaciones internas de gestión.
     *
     * @param companyRequestDTO Datos de la empresa a crear
     * @return DTO con los datos de la empresa creada
     * @throws IllegalArgumentException si el taxId o email ya existen
     */
    CompanyResponseDTO createCompany(CompanyRequestDTO companyRequestDTO);

    /**
     * Registra una nueva empresa junto con su usuario administrador inicial.
     * Este método se utiliza durante el proceso de registro público.
     * Crea la empresa, el usuario administrador y envía un email de verificación.
     *
     * @param registrationDTO Datos de la empresa y del administrador
     * @return DTO con los datos de la empresa creada
     * @throws IllegalArgumentException si el taxId o email ya existen
     */
    CompanyResponseDTO registerCompanyWithAdmin(CompanyRegistrationDTO registrationDTO);

    /**
     * Actualiza los datos de una empresa existente.
     *
     * @param id Identificador de la empresa a actualizar
     * @param companyRequestDTO Nuevos datos de la empresa
     * @return DTO con los datos actualizados
     * @throws ResourceNotFoundException si la empresa no existe
     * @throws IllegalArgumentException si el nuevo taxId o email ya están en uso por otra empresa
     */
    CompanyResponseDTO updateCompany(Long id, CompanyRequestDTO companyRequestDTO);

    /**
     * Obtiene una empresa por su identificador numérico.
     *
     * @param id Identificador de la empresa
     * @return DTO con los datos de la empresa
     * @throws ResourceNotFoundException si la empresa no existe
     */
    CompanyResponseDTO getCompanyById(Long id);

    /**
     * Obtiene una empresa por su identificador UUID.
     *
     * @param uuid UUID de la empresa
     * @return DTO con los datos de la empresa
     * @throws ResourceNotFoundException si la empresa no existe
     */
    CompanyResponseDTO getCompanyByUuid(String uuid);

    /**
     * Obtiene todas las empresas registradas en la plataforma.
     *
     * @return Lista de todas las empresas
     */
    List<CompanyResponseDTO> getAllCompanies();

    /**
     * Obtiene todas las empresas filtradas por estado.
     *
     * @param status Estado de las empresas (ACTIVE, INACTIVE, PENDING)
     * @return Lista de empresas con el estado especificado
     */
    List<CompanyResponseDTO> getCompaniesByStatus(Company.CompanyStatus status);

    /**
     * Elimina una empresa de la plataforma.
     * Esta operación elimina en cascada todos los usuarios asociados.
     *
     * @param id Identificador de la empresa a eliminar
     * @throws ResourceNotFoundException si la empresa no existe
     */
    void deleteCompany(Long id);

    /**
     * Cambia el estado de una empresa.
     * Permite activar, desactivar o suspender una empresa.
     *
     * @param id Identificador de la empresa
     * @param status Nuevo estado a asignar (ACTIVE, INACTIVE, PENDING)
     * @return DTO con los datos actualizados de la empresa
     * @throws ResourceNotFoundException si la empresa no existe
     */
    CompanyResponseDTO changeCompanyStatus(Long id, Company.CompanyStatus status);
}