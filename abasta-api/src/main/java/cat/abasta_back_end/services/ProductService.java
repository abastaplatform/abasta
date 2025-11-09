package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.ProductFilterDTO;
import cat.abasta_back_end.dto.ProductRequestDTO;
import cat.abasta_back_end.dto.ProductResponseDTO;
import cat.abasta_back_end.exceptions.*;
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
     * @throws ResourceNotFoundException Si no existeix cap producte amb aquest UUID.
     */
    ProductResponseDTO getProductByUuid(String uuid);

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

    Page<ProductResponseDTO> searchProductsBySupplierWithSearch(String supplierUuid, String text, Pageable pageable);

    Page<ProductResponseDTO> searchProductsBySupplierWithFilter(String supplierUuid, ProductFilterDTO filterDTO, Pageable pageable);

}
