package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.SupplierFilterDTO;
import cat.abasta_back_end.dto.SupplierRequestDTO;
import cat.abasta_back_end.dto.SupplierResponseDTO;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.entities.Supplier;
import cat.abasta_back_end.exceptions.DuplicateResourceException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.CompanyRepository;
import cat.abasta_back_end.repositories.SupplierRepository;
import cat.abasta_back_end.services.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementació concreta del servei de negoci per a la gestió integral de proveïdors.
 * Proporciona tota la lògica de negoci per administrar proveïdors dins del sistema.
 *
 * <p>Aquesta implementació gestiona les operacions CRUD completes per als proveïdors,
 * incloent-hi validacions de negoci, transformacions de dades i gestió d'errors.</p>
 *
 * <p>Les responsabilitats principals inclouen:
 * <ul>
 *   <li>Implementació de totes les operacions definides a {@link SupplierService}</li>
 *   <li>Validació d'integritat de dades abans de persistir</li>
 *   <li>Transformació bidireccional entre entitats i DTOs</li>
 *   <li>Gestió de relacions amb l'entitat Company</li>
 *   <li>Aplicació de regles de negoci específiques del domini</li>
 * </ul>
 * </p>
 *
 * <p>Validacions implementades:
 * <ul>
 *   <li>Verificació d'existència de l'empresa abans de crear/actualitzar</li>
 *   <li>Control d'unicitat del nom de proveïdor dins de cada empresa</li>
 *   <li>Validació de canvis d'empresa en actualitzacions</li>
 * </ul>
 * </p>
 *
 * <p>Gestió transaccional:
 * <ul>
 *   <li>@Transactional per defecte en operacions de modificació</li>
 *   <li>@Transactional(readOnly = true) per operacions de consulta</li>
 *   <li>Rollback automàtic en cas d'error</li>
 *   <li>Optimització de connexions de base de dades</li>
 * </ul>
 * </p>
 *
 * <p>Exemple de flux d'una operació de creació:
 * <pre>
 * 1. Validació de l'empresa associada per UUID
 * 2. Verificació d'unicitat del nom
 * 3. Construcció de l'entitat Supplier
 * 4. Persistència a la base de dades
 * 5. Transformació a DTO de resposta
 * </pre>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 1.0
 * @see SupplierService
 * @see cat.abasta_back_end.entities.Supplier
 * @see cat.abasta_back_end.entities.Company
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final CompanyRepository companyRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public SupplierResponseDTO createSupplier(SupplierRequestDTO supplierRequestDTO) {

        // Verificar que l'empresa existeix
        Company company = companyRepository.findByUuid(supplierRequestDTO.getCompanyUuid())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa no trobada amb UUID: " + supplierRequestDTO.getCompanyUuid()));

        // Verificar que no existeix un proveïdor amb el mateix nom a l'empresa
        if (supplierRepository.existsByCompanyUuidAndNameIgnoreCase(
                supplierRequestDTO.getCompanyUuid(), supplierRequestDTO.getName())) {
            throw new DuplicateResourceException(
                    "Ja existeix un proveïdor amb el nom '" + supplierRequestDTO.getName() +
                            "' a l'empresa especificada");
        }

        // Crear el proveïdor
        Supplier supplier = Supplier.builder()
                .company(company)
                .name(supplierRequestDTO.getName())
                .contactName(supplierRequestDTO.getContactName())
                .email(supplierRequestDTO.getEmail())
                .phone(supplierRequestDTO.getPhone())
                .address(supplierRequestDTO.getAddress())
                .notes(supplierRequestDTO.getNotes())
                .isActive(supplierRequestDTO.getIsActive())
                .build();

        Supplier savedSupplier = supplierRepository.save(supplier);

        return mapToResponseDTO(savedSupplier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public SupplierResponseDTO getSupplierByUuid(String uuid) {
        Supplier supplier = supplierRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Proveïdor no trobat amb UUID: " + uuid));
        return mapToResponseDTO(supplier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SupplierResponseDTO updateSupplier(String uuid, SupplierRequestDTO supplierRequestDTO) {
        Supplier existingSupplier = supplierRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Proveïdor no trobat amb UUID: " + uuid));

        // Verificar que l'empresa existeix si s'ha canviat
        if (!existingSupplier.getCompany().getUuid().equals(supplierRequestDTO.getCompanyUuid())) {
            Company company = companyRepository.findByUuid(supplierRequestDTO.getCompanyUuid())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Empresa no trobada amb UUID: " + supplierRequestDTO.getCompanyUuid()));
            existingSupplier.setCompany(company);
        }

        // Verificar duplicats de nom si s'ha canviat el nom
        if (!existingSupplier.getName().equalsIgnoreCase(supplierRequestDTO.getName())) {
            if (supplierRepository.existsByCompanyUuidAndNameIgnoreCaseAndUuidNot(
                    supplierRequestDTO.getCompanyUuid(), supplierRequestDTO.getName(), uuid)) {
                throw new DuplicateResourceException(
                        "Ja existeix un proveïdor amb el nom '" + supplierRequestDTO.getName() +
                                "' a l'empresa especificada");
            }
        }

        // Actualitzar les dades
        existingSupplier.setName(supplierRequestDTO.getName());
        existingSupplier.setContactName(supplierRequestDTO.getContactName());
        existingSupplier.setEmail(supplierRequestDTO.getEmail());
        existingSupplier.setPhone(supplierRequestDTO.getPhone());
        existingSupplier.setAddress(supplierRequestDTO.getAddress());
        existingSupplier.setNotes(supplierRequestDTO.getNotes());
        existingSupplier.setIsActive(supplierRequestDTO.getIsActive());

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);

        return mapToResponseDTO(updatedSupplier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getSuppliersByCompanyUuid(String companyUuid) {

        // Verificar que l'empresa existeix
        if (!companyRepository.existsByUuid(companyUuid)) {
            throw new ResourceNotFoundException("Empresa no trobada amb UUID: " + companyUuid);
        }

        List<Supplier> suppliers = supplierRepository.findByCompanyUuid(companyUuid);
        return suppliers.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SupplierResponseDTO toggleSupplierStatus(String uuid, Boolean isActive) {
        Supplier supplier = supplierRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Proveïdor no trobat amb UUID: " + uuid));

        supplier.setIsActive(isActive);
        Supplier updatedSupplier = supplierRepository.save(supplier);

        return mapToResponseDTO(updatedSupplier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponseDTO> searchSuppliersByCompanyAndName(String companyUuid, String name, Pageable pageable) {

        // Verificar que l'empresa es troba
        Company company = companyRepository.findByUuid(companyUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no trobada amb UUID: " + companyUuid));

        // Usar consulta personalitzada per cercar per company i nom
        Page<Supplier> suppliers = supplierRepository.findByCompanyIdAndNameContaining(
                company.getId(), name, pageable);
        // Implementar filtrat manual o crear consulta personalitzada
        return suppliers.map(this::mapToResponseDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public Page<SupplierResponseDTO> searchSuppliersWithFilters(SupplierFilterDTO filterDTO, Pageable pageable) {

        // Verificar que l'empresa existeix i obtenir l'ID
        Company company = companyRepository.findByUuid(filterDTO.getCompanyUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no trobada amb UUID: " + filterDTO.getCompanyUuid()));

        // Usar el mètode del repositori amb tots els filtres expandits
        Page<Supplier> suppliers = supplierRepository.findSuppliersWithFilters(
                company.getId(),
                filterDTO.getName(),
                filterDTO.getContactName(),
                filterDTO.getEmail(),
                filterDTO.getPhone(),
                filterDTO.getAddress(),
                filterDTO.getNotes(),
                filterDTO.getIsActive(),
                filterDTO.getCreatedAfter(),
                filterDTO.getCreatedBefore(),
                filterDTO.getUpdatedAfter(),
                filterDTO.getUpdatedBefore(),
                pageable
        );

        return suppliers.map(this::mapToResponseDTO);
    }

    /**
     * Mapa una entitat Supplier a un DTO de resposta.
     *
     * @param supplier l'entitat a mapar
     * @return el DTO de resposta
     */
    private SupplierResponseDTO mapToResponseDTO(Supplier supplier) {
        return SupplierResponseDTO.builder()
                .uuid(supplier.getUuid())
                .companyUuid(supplier.getCompany().getUuid())
                .companyName(supplier.getCompany().getName())
                .name(supplier.getName())
                .contactName(supplier.getContactName())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .notes(supplier.getNotes())
                .isActive(supplier.getIsActive())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
}