package cat.abasta_back_end.services;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.exceptions.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


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

    /**
     * Realitza la cerca de productes associats a un usuari/companyia
     * <p>
     * Aquesta operació permet llistat els productes d'un usuari a través
     * de la companyia a la qual pertany. Les dades les agafem de l'usuari loginat.
     * Retorna amb criteris de paginació i ordenació.
     * </p>
     * @param pageable Objecte {@link Pageable} que defineix la paginació i l'ordenació dels resultats.
     * @return Una pàgina de {@link ProductResponseDTO} amb el llistat de tots els productes
     */
    Page<ProductResponseDTO> listProductsByCompany(Pageable pageable);

    /**
     * Realitza una cerca bàsica de productes associats a un proveïdor concret.
     * <p>
     * Aquesta operació permet cercar productes pel seu nom o altres camps de text
     * relacionats amb el proveïdor, aplicant criteris de paginació i ordenació.
     * </p>
     *
     * @param supplierUuid Identificador únic (UUID) del proveïdor.
     * @param text Cadena de text utilitzada per filtrar els resultats (nom, descripció, etc.).
     * @param pageable Objecte {@link Pageable} que defineix la paginació i l'ordenació dels resultats.
     * @return Una pàgina de {@link ProductResponseDTO} que compleix els criteris de cerca.
     */
    Page<ProductResponseDTO> searchProductsBySupplierWithSearch(String supplierUuid, String text, Pageable pageable);

    /**
     * Realitza una cerca avançada de productes amb múltiples filtres.
     * <p>
     * Aquesta operació permet aplicar filtres combinats sobre diversos camps
     * del producte (com nom, categoria, estat actiu, dates de creació/modificació, etc.),
     * així com paràmetres de paginació i ordenació.
     * </p>
     *
     * @param supplierUuid Identificador únic (UUID) del proveïdor associat als productes.
     * @param filterDTO Objecte {@link ProductFilterDTO} amb tots els paràmetres de filtratge.
     * @param pageable Objecte {@link Pageable} per definir la paginació i ordenació dels resultats.
     * @return Una pàgina de {@link ProductResponseDTO} amb els productes que compleixen els filtres especificats.
     */
    Page<ProductResponseDTO> searchProductsBySupplierWithFilter(String supplierUuid, ProductFilterDTO filterDTO, Pageable pageable);

    /**
     * Desa una imatge associada a un producte existent.
     * <p>
     * Aquesta operació rep un fitxer d'imatge, el desa en el sistema de fitxers
     * i actualitza el camp {@code imageUrl} del producte amb la ruta resultant.
     * </p>
     *
     * @param productUuid Identificador únic (UUID) del producte al qual es vol associar la imatge.
     * @param file Fitxer d'imatge carregat pel client mitjançant multipart/form-data.
     * @return La ruta o URL pública de la imatge guardada.
     * @throws ResourceNotFoundException Si no existeix cap producte amb el UUID indicat.
     * @throws RuntimeException Si es produeix un error durant la càrrega o el desament de la imatge.
     */
    String saveProductImage(String productUuid, MultipartFile file);

}
