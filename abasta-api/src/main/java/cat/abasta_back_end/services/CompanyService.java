package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyRequestDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;
import cat.abasta_back_end.exceptions.DuplicateResourceException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;

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
     * Obté la informació d'una empresa pel seu identificador UUID.
     *
     * @param uuid l'identificador únic (UUID) de l'empresa a recuperar
     * @return CompanyResponseDTO amb la informació completa de l'empresa
     * @throws ResourceNotFoundException si no existeix cap empresa amb l'UUID proporcionat
     */
    CompanyResponseDTO getCompanyByUuid(String uuid);

    /**
     * Actualitza la informació d'una empresa existent.
     * Permet modificar les dades d'una empresa identificada pel seu UUID.
     * Si el NIF/CIF canvia, es valida que no existeixi en una altra empresa.
     *
     * @param uuid l'identificador únic (UUID) de l'empresa a actualitzar
     * @param companyRequestDTO objecte amb les noves dades de l'empresa
     * @return CompanyResponseDTO amb la informació actualitzada de l'empresa
     * @throws ResourceNotFoundException si no existeix cap empresa amb l'UUID proporcionat
     * @throws DuplicateResourceException si el nou NIF/CIF ja està assignat a una altra empresa
     */
    CompanyResponseDTO updateCompany(String uuid, CompanyRequestDTO companyRequestDTO);
}