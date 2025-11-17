package cat.abasta_back_end.dto;

import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaris per PagedResponseDTO.
 * Cobreix tota la funcionalitat de creació, formatat i mapeig des de Spring Data Page.
 *
 * @author Enrique Pérez
 * @version 1.0
 */
@DisplayName("PagedResponseDTO Tests")
class PagedResponseDTOTest {

    // Dades de test
    private List<String> sampleData;
    private List<String> emptyData;

    @BeforeEach
    void setUp() {
        sampleData = Arrays.asList("Element 1", "Element 2", "Element 3");
        emptyData = Collections.emptyList();
    }

    @Nested
    @DisplayName("Tests del mètode of() - Conversió des de Page")
    class OfMethodTests {

        @Test
        @DisplayName("Hauria de crear PagedResponseDTO des de Page amb dades")
        void should_CreatePagedResponseDTO_When_PageHasData() {
            // Given
            Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
            Page<String> page = new PageImpl<>(sampleData, pageable, 23); // Total 23 elements

            // When
            PagedResponseDTO<String> result = PagedResponseDTO.of(page);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEqualTo(sampleData);
            assertThat(result.getContent()).hasSize(3);

            PagedResponseDTO.PageableInfo pageableInfo = result.getPageable();
            assertThat(pageableInfo).isNotNull();
            assertThat(pageableInfo.getPage()).isEqualTo(0);
            assertThat(pageableInfo.getSize()).isEqualTo(10);
            assertThat(pageableInfo.getTotalElements()).isEqualTo(23);
            assertThat(pageableInfo.getTotalPages()).isEqualTo(3); // 23 / 10 = 3 pàgines
            assertThat(pageableInfo.getNumberOfElements()).isEqualTo(3); // Elements en aquesta pàgina
            assertThat(pageableInfo.isFirst()).isTrue();
            assertThat(pageableInfo.isLast()).isFalse();
            assertThat(pageableInfo.isEmpty()).isFalse();
            assertThat(pageableInfo.getSort()).isEqualTo("name,asc");
        }

        @Test
        @DisplayName("Hauria de crear PagedResponseDTO amb pàgina buida")
        void should_CreatePagedResponseDTO_When_PageIsEmpty() {
            // Given
            Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
            Page<String> page = new PageImpl<>(emptyData, pageable, 0);

            // When
            PagedResponseDTO<String> result = PagedResponseDTO.of(page);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();

            PagedResponseDTO.PageableInfo pageableInfo = result.getPageable();
            assertThat(pageableInfo.getPage()).isEqualTo(0);
            assertThat(pageableInfo.getSize()).isEqualTo(10);
            assertThat(pageableInfo.getTotalElements()).isEqualTo(0);
            assertThat(pageableInfo.getTotalPages()).isEqualTo(0); // Pàgina buida retorna 0
            assertThat(pageableInfo.getNumberOfElements()).isEqualTo(0);
            assertThat(pageableInfo.isFirst()).isTrue();
            assertThat(pageableInfo.isLast()).isTrue();
            assertThat(pageableInfo.isEmpty()).isTrue();
            assertThat(pageableInfo.getSort()).isEqualTo("unsorted");
        }

        @Test
        @DisplayName("Hauria de crear PagedResponseDTO per la segona pàgina")
        void should_CreatePagedResponseDTO_When_SecondPage() {
            // Given
            Pageable pageable = PageRequest.of(1, 2, Sort.by("id").descending());
            Page<String> page = new PageImpl<>(
                    Arrays.asList("Element 3", "Element 4"), // Segona pàgina
                    pageable,
                    10 // Total 10 elements
            );

            // When
            PagedResponseDTO<String> result = PagedResponseDTO.of(page);

            // Then
            PagedResponseDTO.PageableInfo pageableInfo = result.getPageable();
            assertThat(pageableInfo.getPage()).isEqualTo(1); // Segona pàgina (0-indexed)
            assertThat(pageableInfo.getSize()).isEqualTo(2);
            assertThat(pageableInfo.getTotalElements()).isEqualTo(10);
            assertThat(pageableInfo.getTotalPages()).isEqualTo(5); // 10 / 2 = 5 pàgines
            assertThat(pageableInfo.getNumberOfElements()).isEqualTo(2);
            assertThat(pageableInfo.isFirst()).isFalse();
            assertThat(pageableInfo.isLast()).isFalse();
            assertThat(pageableInfo.isEmpty()).isFalse();
            assertThat(pageableInfo.getSort()).isEqualTo("id,desc");
        }

        @Test
        @DisplayName("Hauria de crear PagedResponseDTO per l'última pàgina")
        void should_CreatePagedResponseDTO_When_LastPage() {
            // Given
            Pageable pageable = PageRequest.of(2, 5, Sort.by("createdAt").ascending());
            Page<String> page = new PageImpl<>(
                    Collections.singletonList("Últim element"), // Última pàgina amb 1 element
                    pageable,
                    11 // Total 11 elements
            );

            // When
            PagedResponseDTO<String> result = PagedResponseDTO.of(page);

            // Then
            PagedResponseDTO.PageableInfo pageableInfo = result.getPageable();
            assertThat(pageableInfo.getPage()).isEqualTo(2);
            assertThat(pageableInfo.getSize()).isEqualTo(5);
            assertThat(pageableInfo.getTotalElements()).isEqualTo(11);
            assertThat(pageableInfo.getTotalPages()).isEqualTo(3); // 11 / 5 = 3 pàgines
            assertThat(pageableInfo.getNumberOfElements()).isEqualTo(1); // Només 1 element en aquesta pàgina
            assertThat(pageableInfo.isFirst()).isFalse();
            assertThat(pageableInfo.isLast()).isTrue();
            assertThat(pageableInfo.isEmpty()).isFalse();
        }
    }

    @Nested
    @DisplayName("Tests de formatejat d'ordenació")
    class SortFormattingTests {

        @Test
        @DisplayName("Hauria de formatar ordenació ascendent simple")
        void should_FormatAscendingSort() {
            // Given
            Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
            Page<String> page = new PageImpl<>(sampleData, pageable, 3);

            // When
            PagedResponseDTO<String> result = PagedResponseDTO.of(page);

            // Then
            assertThat(result.getPageable().getSort()).isEqualTo("name,asc");
        }

        @Test
        @DisplayName("Hauria de formatar ordenació descendent simple")
        void should_FormatDescendingSort() {
            // Given
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
            Page<String> page = new PageImpl<>(sampleData, pageable, 3);

            // When
            PagedResponseDTO<String> result = PagedResponseDTO.of(page);

            // Then
            assertThat(result.getPageable().getSort()).isEqualTo("createdAt,desc");
        }

        @Test
        @DisplayName("Hauria de formatar ordenació múltiple")
        void should_FormatMultipleSort() {
            // Given
            Sort multiSort = Sort.by("name").ascending().and(Sort.by("createdAt").descending());
            Pageable pageable = PageRequest.of(0, 10, multiSort);
            Page<String> page = new PageImpl<>(sampleData, pageable, 3);

            // When
            PagedResponseDTO<String> result = PagedResponseDTO.of(page);

            // Then
            assertThat(result.getPageable().getSort()).isEqualTo("name,asc;createdAt,desc");
        }

        @Test
        @DisplayName("Hauria de retornar 'unsorted' quan no hi ha ordenació")
        void should_ReturnUnsorted_When_NoSort() {
            // Given
            Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
            Page<String> page = new PageImpl<>(sampleData, pageable, 3);

            // When
            PagedResponseDTO<String> result = PagedResponseDTO.of(page);

            // Then
            assertThat(result.getPageable().getSort()).isEqualTo("unsorted");
        }

        @Test
        @DisplayName("Hauria de formatar ordenació amb múltiples camps complexa")
        void should_FormatComplexMultipleSort() {
            // Given
            Sort complexSort = Sort.by("priority").descending()
                    .and(Sort.by("name").ascending())
                    .and(Sort.by("updatedAt").descending());
            Pageable pageable = PageRequest.of(0, 10, complexSort);
            Page<String> page = new PageImpl<>(sampleData, pageable, 3);

            // When
            PagedResponseDTO<String> result = PagedResponseDTO.of(page);

            // Then
            assertThat(result.getPageable().getSort())
                    .isEqualTo("priority,desc;name,asc;updatedAt,desc");
        }
    }

    @Nested
    @DisplayName("Tests dels constructors i builders")
    class ConstructorAndBuilderTests {

        @Test
        @DisplayName("Hauria de crear PagedResponseDTO amb builder principal")
        void should_CreateWithMainBuilder() {
            // Given
            PagedResponseDTO.PageableInfo pageableInfo = PagedResponseDTO.PageableInfo.builder()
                    .page(0)
                    .size(10)
                    .sort("name,asc")
                    .totalPages(1)
                    .totalElements(3)
                    .numberOfElements(3)
                    .first(true)
                    .last(true)
                    .empty(false)
                    .build();

            // When
            PagedResponseDTO<String> result = PagedResponseDTO.<String>builder()
                    .content(sampleData)
                    .pageable(pageableInfo)
                    .build();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEqualTo(sampleData);
            assertThat(result.getPageable()).isEqualTo(pageableInfo);
            assertThat(result.getPageable().getPage()).isEqualTo(0);
            assertThat(result.getPageable().getTotalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("Hauria de crear PagedResponseDTO amb constructor per defecte")
        void should_CreateWithDefaultConstructor() {
            // When
            PagedResponseDTO<String> result = new PagedResponseDTO<>();
            PagedResponseDTO.PageableInfo pageableInfo = new PagedResponseDTO.PageableInfo();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isNull();
            assertThat(result.getPageable()).isNull();

            assertThat(pageableInfo).isNotNull();
            assertThat(pageableInfo.getPage()).isEqualTo(0);
            assertThat(pageableInfo.getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("Hauria de crear PagedResponseDTO amb constructor amb tots els arguments")
        void should_CreateWithAllArgsConstructor() {
            // Given
            PagedResponseDTO.PageableInfo pageableInfo = new PagedResponseDTO.PageableInfo(
                    0, 10, "name,asc", 2, 15, 10, true, false, false
            );

            // When
            PagedResponseDTO<String> result = new PagedResponseDTO<>(sampleData, pageableInfo);

            // Then
            assertThat(result.getContent()).isEqualTo(sampleData);
            assertThat(result.getPageable()).isEqualTo(pageableInfo);
            assertThat(result.getPageable().getPage()).isEqualTo(0);
            assertThat(result.getPageable().getSize()).isEqualTo(10);
            assertThat(result.getPageable().getTotalElements()).isEqualTo(15);
            assertThat(result.getPageable().getTotalPages()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Tests amb diferents tipus de dades")
    class DifferentDataTypesTests {

        @Test
        @DisplayName("Hauria de funcionar amb objectes personalitzats")
        void should_WorkWithCustomObjects() {
            // Given
            TestSupplier supplier1 = new TestSupplier("1", "Proveïdor A");
            TestSupplier supplier2 = new TestSupplier("2", "Proveïdor B");
            List<TestSupplier> suppliers = Arrays.asList(supplier1, supplier2);

            Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
            Page<TestSupplier> page = new PageImpl<>(suppliers, pageable, 2);

            // When
            PagedResponseDTO<TestSupplier> result = PagedResponseDTO.of(page);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Proveïdor A");
            assertThat(result.getContent().get(1).getName()).isEqualTo("Proveïdor B");
            assertThat(result.getPageable().getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("Hauria de funcionar amb números")
        void should_WorkWithNumbers() {
            // Given
            List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
            Pageable pageable = PageRequest.of(0, 5, Sort.unsorted());
            Page<Integer> page = new PageImpl<>(numbers, pageable, 5);

            // When
            PagedResponseDTO<Integer> result = PagedResponseDTO.of(page);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).containsExactly(1, 2, 3, 4, 5);
            assertThat(result.getPageable().getTotalElements()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("Tests d'escenaris extrems")
    class EdgeCaseTests {

        @Test
        @DisplayName("Hauria de gestionar pàgina única amb menys elements que la mida")
        void should_HandleSinglePageWithFewerElements() {
            // Given
            Pageable pageable = PageRequest.of(0, 100, Sort.by("id").ascending());
            Page<String> page = new PageImpl<>(sampleData, pageable, 3); // Només 3 elements amb mida 100

            // When
            PagedResponseDTO<String> result = PagedResponseDTO.of(page);

            // Then
            PagedResponseDTO.PageableInfo pageableInfo = result.getPageable();
            assertThat(pageableInfo.getPage()).isEqualTo(0);
            assertThat(pageableInfo.getSize()).isEqualTo(100);
            assertThat(pageableInfo.getTotalElements()).isEqualTo(3);
            assertThat(pageableInfo.getTotalPages()).isEqualTo(1);
            assertThat(pageableInfo.getNumberOfElements()).isEqualTo(3);
            assertThat(pageableInfo.isFirst()).isTrue();
            assertThat(pageableInfo.isLast()).isTrue();
            assertThat(pageableInfo.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("Hauria de gestionar mida de pàgina d'1 element")
        void should_HandlePageSizeOfOne() {
            // Given
            Pageable pageable = PageRequest.of(2, 1, Sort.by("name").descending());
            Page<String> page = new PageImpl<>(
                    Collections.singletonList("Element 3"), // Tercera pàgina, 1 element
                    pageable,
                    5 // Total 5 elements
            );

            // When
            PagedResponseDTO<String> result = PagedResponseDTO.of(page);

            // Then
            PagedResponseDTO.PageableInfo pageableInfo = result.getPageable();
            assertThat(pageableInfo.getPage()).isEqualTo(2);
            assertThat(pageableInfo.getSize()).isEqualTo(1);
            assertThat(pageableInfo.getTotalElements()).isEqualTo(5);
            assertThat(pageableInfo.getTotalPages()).isEqualTo(5);
            assertThat(pageableInfo.getNumberOfElements()).isEqualTo(1);
            assertThat(pageableInfo.isFirst()).isFalse();
            assertThat(pageableInfo.isLast()).isFalse();
            assertThat(pageableInfo.isEmpty()).isFalse();
        }
    }

    // Classe auxiliar per tests
    @Getter
    private static class TestSupplier {
        private final String id;
        private final String name;

        public TestSupplier(String id, String name) {
            this.id = id;
            this.name = name;
        }

    }
}