package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.entities.Company;

import java.util.List;

/**
 * Servei per la gestió d'empreses a la plataforma.
 * Defineix les operacions CRUD sobre empreses.
 *
 * @author Dani Garcia
 * @version 1.0
 */
public interface CompanyService {

    /**
     * Registra una nova empresa juntament amb usuari administrador inicial.
     * Aquest mètode s'utilitza durant el procés de registre públic.
     * Crea l'empresa, l'usuari administrador i envia email de verificació.
     *
     * @param registrationDTO Dades de l'empresa i l'administrador
     * @return DTO amb les dades de l'empresa creada
     * @throws IllegalArgumentException si el taxId o email ja existeix.
     */
    CompanyResponseDTO registerCompanyWithAdmin(CompanyRegistrationDTO registrationDTO);

    /**
     * Obté una empresa per el seu UUID
     *
     * @param uuid UUID de l'empresa
     * @return DTO amb les dades de l'empresa
     * @throws ResourceNotFoundException si l'empresa no existeix
     */
    CompanyResponseDTO getCompanyByUuid(String uuid);

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