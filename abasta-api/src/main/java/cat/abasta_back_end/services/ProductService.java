package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.ProductRequestDTO;
import cat.abasta_back_end.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfície que defineix les operacions de negoci per a la gestió dels productes.
 * <p>
 * Aquesta capa actua com a contracte entre el controlador i la implementació del servei,
 * definint les funcionalitats disponibles relacionades amb la creació i gestió de productes.
 * </p>
 *
 * @author Daniel Garcia
 * @since 1.0
 */
public interface ProductService {

    /**
     * Crea un nou producte dins del sistema.
     * <p>
     * Aquesta operació rep un {@link ProductRequestDTO} amb les dades necessàries per crear
     * el producte i retorna un {@link ProductResponseDTO} amb la informació resultant.
     * </p>
     *
     * @param productRequestDTO dades del producte a crear
     * @return el producte creat en format {@link ProductResponseDTO}
     */
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);

    /**
     * Obté un producte pel seu UUID.
     *
     * @param uuid Identificador únic del producte.
     * @return El producte corresponent com a {@link ProductResponseDTO}.
     * @throws EntityNotFoundException Si no existeix cap producte amb aquest UUID.
     */
    ProductResponseDTO getProductByUuid(String uuid);

    /**
     * Recupera una pàgina de productes d'un proveïdor determinat.
     *
     * @param supplierId identificador del proveïdor
     * @param pageable   paràmetres de paginació (page, size, sort)
     * @return pàgina de {@link ProductResponseDTO}
     */
    Page<ProductResponseDTO> listProductsBySupplier(String supplierUuid, Pageable pageable);

    /**
     * Actualitza les dades d’un producte existent identificat pel seu UUID.
     *
     * @param uuid identificador únic del producte a actualitzar
     * @param productRequestDTO dades noves per al producte
     * @return {@link ProductResponseDTO} amb la informació actualitzada
     * @throws ResourceNotFoundException si no existeix cap producte amb el UUID indicat
     */
    ProductResponseDTO updateProduct(String uuid, ProductRequestDTO productRequestDTO);

    /**
     * Desactiva un producte establint el camp {@code isActive} a {@code false}.
     * <p>
     * Aquesta operació actua com una eliminació lògica: el producte no s'elimina
     * físicament de la base de dades, però deixa d'estar disponible a nivell d'aplicació.
     * </p>
     *
     * @param uuid Identificador únic del producte.
     * @return {@link ProductResponseDTO} amb el producte desactivat.
     * @throws ResourceNotFoundException Si no existeix cap producte amb el UUID indicat.
     */
    ProductResponseDTO deactivateProduct(String uuid);

    /**
     * Cerca i filtra productes segons diversos criteris opcionals.
     * <p>
     * Aquesta operació permet cercar productes pel seu nom, categoria o proveïdor.
     * Tots els filtres són opcionals, i els resultats sempre inclouen només productes actius.
     * </p>
     *
     * @param name         (opcional) nom parcial o complet del producte.
     * @param category     (opcional) categoria del producte.
     * @param supplierUuid (opcional) UUID del proveïdor.
     * @param pageable     configuració de paginació.
     * @return una pàgina de {@link ProductResponseDTO} amb els productes trobats.
     */
    Page<ProductResponseDTO> searchProducts(String name, String category, String supplierUuid, Pageable pageable);


}
