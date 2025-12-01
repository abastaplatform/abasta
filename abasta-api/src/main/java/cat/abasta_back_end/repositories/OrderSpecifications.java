package cat.abasta_back_end.repositories;

import cat.abasta_back_end.entities.Order;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitària que conté especificacions per construir consultes dinàmiques
 * sobre l'entitat {@link Order}. Permet aplicar filtres opcionals de manera
 * flexible mitjançant l'API de Criteria i Specifications.
 *
 * <p>Aquesta classe permet cercar per múltiples camps:
 * companyId, supplierId, userId, searchText global, nom, notes, estat,
 * imports mínim/màxim, dates de creació, actualització i entrega.</p>
 *
 * @author Daniel Garcia
 * @version 2.0
 */
public class OrderSpecifications {

    /**
     * Genera una {@link Specification} que aplica múltiples filtres de manera dinàmica
     * a l'entitat {@link Order}, només afegint les condicions dels paràmetres que
     * no siguin nulls o buits.
     *
     * <p>Permet filtrar per:
     * <ul>
     *     <li>companyId – Empresa propietària</li>
     *     <li>supplierId – Proveïdor</li>
     *     <li>userId – Usuari assignat</li>
     *     <li>searchText – Cerca global en nom i notes</li>
     *     <li>name – Filtre específic pel nom</li>
     *     <li>notes – Filtre específic per notes (CLOB)</li>
     *     <li>status – Estat de la comanda</li>
     *     <li>minAmount – Import mínim</li>
     *     <li>maxAmount – Import màxim</li>
     *     <li>deliveryDateFrom / deliveryDateTo – Rang de dates d'entrega</li>
     *     <li>createdAtFrom / createdAtTo – Rang de dates de creació</li>
     *     <li>updatedAtFrom / updatedAtTo – Rang de dates d'actualització</li>
     * </ul>
     * </p>
     *
     * @param orderId ID de la comanda
     * @param companyId ID de l'empresa a filtrar
     * @param supplierId ID del proveïdor
     * @param userId ID de l’usuari creador/assignat
     * @param searchText Text de cerca global (nom i notes)
     * @param name Filtre pel nom
     * @param notes Filtre per notes
     * @param status Estat de la comanda
     * @param minAmount Import mínim
     * @param maxAmount Import màxim
     * @param deliveryDateFrom Data mínima d'entrega
     * @param deliveryDateTo Data màxima d'entrega
     * @param createdAtFrom Data mínima de creació
     * @param createdAtTo Data màxima de creació
     * @param updatedAtFrom Data mínima d'actualització
     * @param updatedAtTo Data màxima d'actualització
     * @return Specification<Order> amb tots els filtres aplicats
     */
    public static Specification<Order> filterOrders(
            Long orderId,
            Long companyId,
            Long supplierId,
            Long userId,
            String searchText,
            String name,
            String notes,
            Order.OrderStatus status,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDate deliveryDateFrom,
            LocalDate deliveryDateTo,
            LocalDateTime createdAtFrom,
            LocalDateTime createdAtTo,
            LocalDateTime updatedAtFrom,
            LocalDateTime updatedAtTo
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (orderId != null)
                predicates.add(cb.equal(root.get("id"), orderId));

            if (companyId != null)
                predicates.add(cb.equal(root.get("company").get("id"), companyId));

            if (supplierId != null)
                predicates.add(cb.equal(root.get("supplier").get("id"), supplierId));

            if (userId != null)
                predicates.add(cb.equal(root.get("user").get("id"), userId));

            // -----------------------------
            //  SEARCH TEXT (global)
            //  Compatible amb CLOB/TEXT gràcies a .as(String.class)
            // -----------------------------
            if (searchText != null && !searchText.isBlank()) {
                String like = "%" + searchText.toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), like);
                Predicate notesLike = cb.like(cb.lower(root.get("notes").as(String.class)), like);
                predicates.add(cb.or(nameLike, notesLike));
            }

            // Filtres individuals
            if (name != null && !name.isBlank())
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));

            if (notes != null && !notes.isBlank())
                predicates.add(cb.like(cb.lower(root.get("notes").as(String.class)), "%" + notes.toLowerCase() + "%"));

            // Status predeterminat
            if (status != null) {
                // Si tenim status, filtrem per aquest
                predicates.add(cb.equal(root.get("status"), status));
            } else {
                // si tenim status, recuperem tots excepte els que estan en deleted.
                predicates.add(cb.notEqual(root.get("status"), Order.OrderStatus.DELETED));
            }

            if (minAmount != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalAmount"), minAmount));

            if (maxAmount != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("totalAmount"), maxAmount));

            // -----------------------------
            //  FILTRES DE DATES
            // -----------------------------
            if (deliveryDateFrom != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("deliveryDate"), deliveryDateFrom));

            if (deliveryDateTo != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("deliveryDate"), deliveryDateTo));

            if (createdAtFrom != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtFrom));

            if (createdAtTo != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdAtTo));

            if (updatedAtFrom != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), updatedAtFrom));

            if (updatedAtTo != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), updatedAtTo));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}