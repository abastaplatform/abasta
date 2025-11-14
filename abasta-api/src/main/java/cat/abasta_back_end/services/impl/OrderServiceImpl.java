package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.OrderItemRequestDTO;
import cat.abasta_back_end.dto.OrderItemResponseDTO;
import cat.abasta_back_end.dto.OrderRequestDTO;
import cat.abasta_back_end.dto.OrderResponseDTO;
import cat.abasta_back_end.entities.*;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.*;
import cat.abasta_back_end.services.NotificationService;
import cat.abasta_back_end.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementació del servei {@link OrderService} per a la gestió de comandes.
 * <p>
 * Aquesta classe conté la lògica de negoci per crear, administrar i enviar comandes
 * dins del sistema. Gestiona tant la creació de la comanda principal com
 * dels seus items associats, assegurant la coherència mitjançant transaccions.
 * Separa la creació (estat PENDING) de l'enviament (estat SENT) per permetre
 * revisar les comandes abans d'enviar-les als proveïdors.
 * </p>
 *
 * <p>Les comandes sempre s'envien per email al proveïdor.</p>
 *
 * @author Daniel Garcia
 * @author Enrique Pérez
 * @version 1.4
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    /**
     * {@inheritDoc}
     *
     * <p>Crea la comanda amb estat PENDING. NO envia cap notificació.
     * Per enviar la comanda, cal cridar després {@link #sendOrder(String)}.</p>
     */
    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {

        // Crear la instància de l'entitat Order
        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setName(orderRequestDTO.getName());
        order.setNotes(orderRequestDTO.getNotes());
        order.setDeliveryDate(orderRequestDTO.getDeliveryDate());
        order.setStatus(Order.OrderStatus.PENDING);

        // Assignar company i usuari
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuari no trobat: " + username));

        order.setUser(user);
        order.setCompany(user.getCompany());

        // Buscar el proveïdor pel UUID
        Supplier supplier = supplierRepository.findByUuid(orderRequestDTO.getSupplierUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Proveïdor no trobat"));
        order.setSupplier(supplier);

        // Crear els items associats a la comanda
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDTO itemDTO : orderRequestDTO.getItems()) {
            OrderItem item = new OrderItem();
            item.setUuid(UUID.randomUUID().toString());
            item.setOrder(order);

            Product product = productRepository.findByUuid(itemDTO.getProductUuid())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producte no trobat: " + itemDTO.getProductUuid()));

            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice() != null ?
                    itemDTO.getUnitPrice() : product.getPrice());
            item.setSubtotal(item.getQuantity().multiply(item.getUnitPrice()));
            item.setNotes(itemDTO.getNotes());

            totalAmount = totalAmount.add(item.getSubtotal());
            orderItems.add(item);
        }

        // Assignar el total i guardar
        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        log.info("Comanda {} creada correctament per l'usuari {} amb estat PENDING",
                order.getUuid(), username);

        return buildOrderResponseDTO(order);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Envia la comanda al proveïdor per email i actualitza l'estat a SENT.</p>
     *
     * <p>Validacions:
     * <ul>
     *   <li>La comanda ha d'existir</li>
     *   <li>La comanda ha d'estar en estat PENDING</li>
     *   <li>El proveïdor ha de tenir email configurat</li>
     * </ul>
     * </p>
     */
    @Override
    @Transactional
    public OrderResponseDTO sendOrder(String orderUuid) {
        log.info("Intentant enviar la comanda {}", orderUuid);

        // Buscar la comanda
        Order order = orderRepository.findByUuid(orderUuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comanda no trobada: " + orderUuid));

        // Validar que està en estat PENDING
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "La comanda " + orderUuid + " no es pot enviar. Estat actual: " + order.getStatus());
        }

        // Enviar notificació per email
        // Si falla, la transacció fa rollback i l'estat no canvia
        notificationService.sendOrderNotification(order);

        // Actualitzar estat (ja es fa dins de notificationService, però per seguretat)
        order.setStatus(Order.OrderStatus.SENT);
        orderRepository.save(order);

        log.info("Comanda {} enviada correctament a {}",
                orderUuid, order.getSupplier().getEmail());

        return buildOrderResponseDTO(order);
    }

    /**
     * Construeix el DTO de resposta a partir d'una entitat Order.
     *
     * @param order l'entitat Order
     * @return el DTO OrderResponseDTO
     */
    private OrderResponseDTO buildOrderResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .uuid(order.getUuid())
                .name(order.getName())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .notes(order.getNotes())
                .deliveryDate(order.getDeliveryDate())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .supplierUuid(order.getSupplier().getUuid())
                .items(order.getItems().stream()
                        .map(item -> OrderItemResponseDTO.builder()
                                .uuid(item.getUuid())
                                .productUuid(item.getProduct() != null ? item.getProduct().getUuid() : null)
                                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .subtotal(item.getSubtotal())
                                .notes(item.getNotes())
                                .build())
                        .toList())
                .build();
    }
}