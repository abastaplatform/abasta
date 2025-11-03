package cat.abasta_back_end.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) per gestionar les respostes de consulta de proveïdors.
 * Conté tota la informació d'un proveïdor que es retorna als clients de l'API.
 *
 * <p>Aquest DTO s'utilitza en les respostes de tots els endpoints de consulta de proveïdors,
 * proporcionant una estructura estandarditzada i segura per a la transferència de dades.</p>
 *
 * <p>Les característiques de la resposta inclouen:
 * <ul>
 *   <li>Identificador públic UUID del proveïdor</li>
 *   <li>Dades de l'empresa associada (UUID i nom) per facilitar la navegació</li>
 *   <li>Informació de contacte completa del proveïdor</li>
 *   <li>Estat d'activitat actual</li>
 *   <li>Metadades d'auditoria (dates de creació i actualització)</li>
 *   <li>Notes addicionals si n'hi ha</li>
 * </ul>
 * </p>
 *
 * <p>Les anotacions de Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor, @Builder)
 * generen automàticament:
 * <ul>
 *   <li>Getters i setters per a tots els camps</li>
 *   <li>Mètodes equals(), hashCode() i toString()</li>
 *   <li>Constructor sense paràmetres</li>
 *   <li>Constructor amb tots els paràmetres</li>
 *   <li>Patró Builder per a la construcció fluent d'objectes</li>
 * </ul>
 * </p>
 *
 * <p>Exemple d'ús amb Builder:
 * <pre>
 * SupplierResponseDTO response = SupplierResponseDTO.builder()
 *     .uuid("550e8400-e29b-41d4-a716-446655440000")
 *     .companyUuid("123e4567-e89b-12d3-a456-426614174000")
 *     .companyName("Abasta SL")
 *     .name("Proveïdors Catalunya SL")
 *     .contactName("Joan Martínez")
 *     .email("joan@provcat.com")
 *     .phone("938765432")
 *     .address("Av. Diagonal 123, Barcelona")
 *     .isActive(true)
 *     .createdAt(LocalDateTime.now())
 *     .updatedAt(LocalDateTime.now())
 *     .build();
 * </pre>
 * </p>
 *
 * <p>Estructura JSON de la resposta:
 * <pre>
 * {
 *   "uuid": "550e8400-e29b-41d4-a716-446655440000",
 *   "companyUuid": "123e4567-e89b-12d3-a456-426614174000",
 *   "companyName": "Abasta SL",
 *   "name": "Proveïdors Catalunya SL",
 *   "contactName": "Joan Martínez",
 *   "email": "joan@provcat.com",
 *   "phone": "938765432",
 *   "address": "Av. Diagonal 123, Barcelona",
 *   "notes": "Proveïdor de materials de construcció",
 *   "isActive": true,
 *   "createdAt": "2024-01-15T10:30:00",
 *   "updatedAt": "2024-01-15T10:30:00"
 * }
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponseDTO {

    /**
     * Identificador UUID únic del proveïdor per a ús públic.
     */
    private String uuid;

    /**
     * UUID de l'empresa associada.
     */
    private String companyUuid;

    /**
     * Nom de l'empresa associada.
     */
    private String companyName;

    /**
     * Nom de l'empresa proveïdora.
     */
    private String name;

    /**
     * Nom de la persona de contacte.
     */
    private String contactName;

    /**
     * Adreça de correu electrònic.
     */
    private String email;

    /**
     * Número de telèfon.
     */
    private String phone;

    /**
     * Adreça física.
     */
    private String address;

    /**
     * Notes addicionals.
     */
    private String notes;

    /**
     * Estat d'activitat del proveïdor.
     */
    private Boolean isActive;

    /**
     * Data de creació.
     */
    private LocalDateTime createdAt;

    /**
     * Data d'última actualització.
     */
    private LocalDateTime updatedAt;
}