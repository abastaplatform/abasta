package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.CompanyRegistrationDTO;
import cat.abasta_back_end.dto.CompanyRequestDTO;
import cat.abasta_back_end.dto.CompanyResponseDTO;

/**
 * Servei per la gestió d'empreses a la plataforma.
 * Defineix les operacions CRUD sobre empreses.
 *
 * @author Dani Garcia
 * @version 1.0
 */
public interface CompanyService {

    /**
     * Procés de registre complet:
     * <ol>
     *   <li>Valida que no existeixi el taxID ni l'email del admin</li>
     *   <li>Crea l'empresa amb estat ACTIVE</li>
     *   <li>Crea l'usuari administrador amb rol ADMIN</li>
     *   <li>Genera un token de verificació amb validesa de 24 hores</li>
     *   <li>Envia email de verificació a l'administrador</li>
     * </ol>
     *
     * El compte del admin queda inactiu (emailVerified=false) fins verificació mitjançant token enviat al correu.
     */
    CompanyResponseDTO registerCompanyWithAdmin(CompanyRegistrationDTO registrationDTO);

    /**
     * Obté la informació d'una empresa pel seu identificador UUID.
     * Operació de només lectura que recupera una empresa de la base de dades
     * i la converteix al DTO de resposta.
     *
     * @return CompanyResponseDTO amb la informació completa de l'empresa
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si no existeix cap empresa amb l'UUID extret del token
     */
    CompanyResponseDTO getCompanyByUuid();

    /**
     * Actualitza la informació d'una empresa existent.
     * Modifica les dades d'una empresa identificada pel seu UUID, validant que no es
     * produeixi duplicació del NIF/CIF si aquest canvia. Tots els camps proporcionats
     * al DTO s'actualitzen, excepte l'estat que només es modifica si es proporciona explícitament.
     *
     * <p>Validacions realitzades:
     * <ul>
     *   <li>Verifica que l'empresa existeixi</li>
     *   <li>Comprova que el nou NIF/CIF no estigui ja assignat a una altra empresa</li>
     *   <li>Actualitza l'estat només si es proporciona al DTO</li>
     * </ul>
     * </p>
     *
     * @param companyRequestDTO objecte amb les noves dades de l'empresa
     * @return CompanyResponseDTO amb la informació actualitzada de l'empresa
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si no existeix cap empresa amb l'UUID extret del token
     * @throws cat.abasta_back_end.exceptions.DuplicateResourceException si el nou NIF/CIF ja està assignat a una altra empresa
     */
    CompanyResponseDTO updateCompany(CompanyRequestDTO companyRequestDTO);
}