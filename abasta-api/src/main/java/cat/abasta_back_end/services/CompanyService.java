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
     * Obté les dades de l'empresa associada a l'usuari autenticat.
     * <p>
     * Només els usuaris amb rol d'administrador poden accedir a aquesta informació.
     * L'empresa es recupera a partir de l'UUID associat a l'usuari autenticat.
     * </p>
     *
     * @return DTO amb les dades de l'empresa
     * @throws cat.abasta_back_end.exceptions.BadRequestException si l'usuari autenticat no té rol d'administrador
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si l'empresa associada a l'usuari no existeix
     */
    CompanyResponseDTO getCompanyByUuid();

    /**
     * Actualitza la informació d'una empresa existent.
     * Modifica les dades d'una empresa identificada pel seu UUID, validant que no es
     * produeixi duplicació del NIF/CIF si aquest canvia. Tots els camps proporcionats
     * al DTO s'actualitzen, excepte l'estat que només es modifica si es proporciona explícitament.
     * Només els usuaris amb rol d'administrador poden actualitzar les dades de l'empresa.
     *
     * <p>Validacions realitzades:
     * <ul>
     *   <li>Valida que el rol sigui administrador</li>
     *   <li>Verifica que l'empresa existeixi</li>
     *   <li>Comprova que el nou NIF/CIF no estigui ja assignat a una altra empresa</li>
     *   <li>Actualitza l'estat només si es proporciona al DTO</li>
     * </ul>
     * </p>
     *
     * @param companyRequestDTO objecte amb les noves dades de l'empresa
     * @return CompanyResponseDTO amb la informació actualitzada de l'empresa
     * @throws cat.abasta_back_end.exceptions.BadRequestException si l'usuari autenticat no té rol d'administrador
     * @throws cat.abasta_back_end.exceptions.ResourceNotFoundException si no existeix cap empresa amb l'UUID extret del token
     * @throws cat.abasta_back_end.exceptions.DuplicateResourceException si el nou NIF/CIF ja està assignat a una altra empresa
     */
    CompanyResponseDTO updateCompany(CompanyRequestDTO companyRequestDTO);
}