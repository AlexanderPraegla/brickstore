package edu.hm.praegla.order.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.hm.praegla.order.dto.CreateOrderDTO;
import edu.hm.praegla.order.entity.Order;
import edu.hm.praegla.order.entity.OrderStatus;
import edu.hm.praegla.order.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "orders", produces = {"application/json"})
@Tag(name = "Order API")
public class OrderController {

    @Value("${spring.gateway.host}")
    private String host;
    @Value("${spring.gateway.port}")
    private String port;
    @Value("${spring.gateway.scheme}")
    private String scheme;

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasAuthority('admins')")
    @GetMapping("open")
    public List<Order> getOpenOrders() {
        return orderService.getOpenOrders();
    }

    @PreAuthorize("hasAuthority('customers')")
    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable long orderId) {
        return orderService.getOrder(orderId);
    }

    @PreAuthorize("hasAuthority('customers')")
    @GetMapping("account/{accountId}")
    public Iterable<Order> getOrdersForAccount(@PathVariable long accountId) {
        return orderService.getOrdersForAccount(accountId);
    }

    @PreAuthorize("hasAuthority('customers')")
    @PutMapping
    public ResponseEntity<?> createOrder(UriComponentsBuilder b, @Valid @RequestBody CreateOrderDTO createOrderDTO) {
        Order order = orderService.createOrder(createOrderDTO);

        UriComponents uriComponents = b.scheme(scheme).host(host).port(port).path("/orders/{orderId}").buildAndExpand(order.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PreAuthorize("hasAuthority('admins')")
    @PostMapping("/status")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody OrderController.OrderStatusUpdateDTO orderStatusUpdateDTO) {
        orderService.updateStatus(orderStatusUpdateDTO.getOrderId(), orderStatusUpdateDTO.getStatus());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('customers')")
    @PostMapping("/{orderId}/cancellation")
    public ResponseEntity<?> cancelOrder(@PathVariable long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static class OrderStatusUpdateDTO {
        @Min(1)
        private long orderId;
        @NotNull
        private OrderStatus status;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CancelOrderDTO {

        @Min(1)
        private long accountId;
        @Min(1)
        private long orderId;
    }
}
