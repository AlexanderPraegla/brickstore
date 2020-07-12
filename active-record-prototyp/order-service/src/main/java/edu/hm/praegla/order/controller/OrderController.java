package edu.hm.praegla.order.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.hm.praegla.order.entity.Order;
import edu.hm.praegla.order.entity.OrderStatus;
import edu.hm.praegla.order.service.OrderService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("open")
    public List<Order> getOpenOrders() {
        return orderService.getOpenOrders();
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable long orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping("account/{accountId}")
    public Iterable<Order> getOrdersForAccount(@PathVariable long accountId) {
        return orderService.getOrdersForAccount(accountId);
    }

    @PutMapping
    public ResponseEntity<?> createOrder(UriComponentsBuilder b, @Valid @RequestBody CreateOrderDTO createOrderDTO) {
        Order order = orderService.createOrder(createOrderDTO.accountId);

        UriComponents uriComponents = b.path("/orders/{orderId}").buildAndExpand(order.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PostMapping("/status")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UpdateOrderStatusDTO updateOrderStatusDTO) {
        orderService.updateStatus(updateOrderStatusDTO.orderId, updateOrderStatusDTO.status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancellation")
    public ResponseEntity<?> cancelOrder(@Valid @RequestBody CancelOrderDTO cancelOrderDTO) {
        orderService.cancelOrder(cancelOrderDTO.orderId);
        return ResponseEntity.ok().build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class UpdateOrderStatusDTO {
        @Min(1)
        private long orderId;
        @NotNull
        public OrderStatus status;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CreateOrderDTO {
        @Min(1)
        public long accountId;
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
