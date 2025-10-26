package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.CompanyRegistrationDTO;
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
     * Registra una nova empresa juntament amb usuari administrador inicial.
     * Aquest mètode s'utilitza durant el procés de registre públic.
     * Crea l'empresa, l'usuari administrador i envia email de verificació.
     *
     * @param registrationDTO Dades de l'empresa i l'administrador
     * @return DTO amb les dades de l'empresa creada
     * @throws IllegalArgumentException si el taxId o email ja existeix.
     */
    CompanyResponseDTO registerCompanyWithAdmin(CompanyRegistrationDTO registrationDTO);

}