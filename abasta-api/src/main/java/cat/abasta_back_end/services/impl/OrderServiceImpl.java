package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.OrderItemRequestDTO;
import cat.abasta_back_end.dto.OrderItemResponseDTO;
import cat.abasta_back_end.dto.OrderRequestDTO;
import cat.abasta_back_end.dto.OrderResponseDTO;
import cat.abasta_back_end.entities.*;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.*;
import cat.abasta_back_end.services.OrderService;
import lombok.RequiredArgsConstructor;
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
 * Aquesta classe conté la lògica de negoci per crear i administrar comandes
 * dins del sistema. Gestiona tant la creació de la comanda principal com
 * dels seus items associats, assegurant la coherència mitjançant transaccions.
 * </p>
 *
 * <p>
 * Els mètodes d’aquesta classe utilitzen els repositoris per accedir i
 * modificar la base de dades.
 * </p>
 *
 * @author Daniel Garcia
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    /**
     * Injecció de dependències - repositoris
     */
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {

        // Crear la instància de l’entitat Order
        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setName(orderRequestDTO.getName());
        order.setNotes(orderRequestDTO.getNotes());
        order.setDeliveryDate(orderRequestDTO.getDeliveryDate());
        order.setNotificationMethod(Order.NotificationMethod.valueOf(orderRequestDTO.getNotificationMethod().toUpperCase()));
        order.setStatus(Order.OrderStatus.PENDING);

        //  Assignar company i usuari
        // Agafar l'usuari autenticat
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuari no trobat: " + username));

        //Long userId = user.getId();

       //User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuari no trobat"));
        order.setUser(user);
        order.setCompany(user.getCompany());

        // Buscar el proveïdor pel UUID rebut
        Supplier supplier = supplierRepository.findByUuid(orderRequestDTO.getSupplierUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Proveïdor no trobat"));
        order.setSupplier(supplier);

        // Llista per acumular els items i calcular el total
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // Crear els items associats a la comanda
        for (OrderItemRequestDTO itemDTO : orderRequestDTO.getItems()) {
            OrderItem item = new OrderItem();
            item.setUuid(UUID.randomUUID().toString());
            item.setOrder(order); // associació bidireccional
            Product product = productRepository.findByUuid(itemDTO.getProductUuid())
                    .orElseThrow(() -> new ResourceNotFoundException("Producte no trobat: " + itemDTO.getProductUuid()));
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice() != null ? itemDTO.getUnitPrice() : BigDecimal.ZERO);
            item.setSubtotal(item.getQuantity().multiply(item.getUnitPrice()));
            item.setNotes(itemDTO.getNotes());

            totalAmount = totalAmount.add(item.getSubtotal());
            orderItems.add(item);
        }

        // Assignar el total calculat i guardar
        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        // Convertir a DTO de resposta
        return OrderResponseDTO.builder()
                .uuid(order.getUuid())
                .name(order.getName())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .notes(order.getNotes())
                .deliveryDate(order.getDeliveryDate())
                .notificationMethod(order.getNotificationMethod() != null ? order.getNotificationMethod().name() : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .supplierUuid(orderRequestDTO.getSupplierUuid()) // per ara, fins que tinguem l’entitat
                .items(orderItems.stream().map(item -> OrderItemResponseDTO.builder()
                                .uuid(item.getUuid())
                                .productUuid(item.getProduct() != null ? item.getProduct().getUuid() : null)
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .subtotal(item.getSubtotal())
                                .notes(item.getNotes())
                                .build())
                        .toList())
                .build();
    }
}
