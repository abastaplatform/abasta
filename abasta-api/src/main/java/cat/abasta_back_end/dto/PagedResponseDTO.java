package cat.abasta_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * DTO per encapsular respostes paginades de forma consistent.
 * Evita la serialització directa de PageImpl i proporciona una estructura JSON estable.
 *
 * <p>Aquest DTO substitueix l'ús directe de Spring Data Page en les respostes REST,
 * garantint una estructura JSON consistent i estable entre versions de Spring.</p>
 *
 * @param <T> tipus de les dades contingudes a la pàgina
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseDTO<T> {

    /**
     * Contingut de la pàgina actual.
     */
    private List<T> content;

    /**
     * Informació completa de paginació unificada.
     */
    private PageableInfo pageable;

    /**
     * Classe interna per encapsular tota la informació de paginació.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageableInfo {

        // ===== INFORMACIÓ DE LA PÀGINA ACTUAL =====
        /**
         * Número de pàgina actual (començant per 0).
         */
        private int page;

        /**
         * Mida de la pàgina (número d'elements per pàgina).
         */
        private int size;

        /**
         * Informació d'ordenació formatada.
         */
        private String sort;

        // ===== ESTADÍSTIQUES GENERALS =====
        /**
         * Número total de pàgines disponibles.
         */
        private int totalPages;

        /**
         * Número total d'elements en totes les pàgines.
         */
        private long totalElements;

        /**
         * Número d'elements en la pàgina actual.
         */
        private int numberOfElements;

        /**
         * Indica si és la primera pàgina.
         */
        private boolean first;

        /**
         * Indica si és l'última pàgina.
         */
        private boolean last;

        /**
         * Indica si la pàgina està buida.
         */
        private boolean empty;
    }

    /**
     * Crea un PagedResponseDTO a partir d'un Page de Spring Data.
     * Tota la informació de paginació es consolida en un sol objecte.
     *
     * @param page el Page original
     * @param <T> tipus de dades
     * @return PagedResponseDTO amb tota la informació unificada
     */
    public static <T> PagedResponseDTO<T> of(Page<T> page) {
        PageableInfo pageableInfo = PageableInfo.builder()
                .page(page.getPageable().getPageNumber())
                .size(page.getPageable().getPageSize())
                .sort(formatSort(page.getPageable().getSort()))
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .numberOfElements(page.getNumberOfElements())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();

        return PagedResponseDTO.<T>builder()
                .content(page.getContent())
                .pageable(pageableInfo)
                .build();
    }

    /**
     * Formata la informació d'ordenació en un string llegible.
     *
     * @param sort l'objecte Sort
     * @return string formatat amb la informació d'ordenació
     */
    private static String formatSort(Sort sort) {
        if (sort.isEmpty()) {
            return "unsorted";
        }

        return sort.stream()
                .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                .reduce((a, b) -> a + ";" + b)
                .orElse("unsorted");
    }
}