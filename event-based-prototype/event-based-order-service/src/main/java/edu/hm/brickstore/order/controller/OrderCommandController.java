package edu.hm.brickstore.order.controller;

import edu.hm.brickstore.order.dto.OrderStatusUpdateDTO;
import edu.hm.brickstore.order.entity.Order;
import edu.hm.brickstore.order.service.OrderCommandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "orders", produces = {"application/json"})
@Tag(name = "Order command API")
public class OrderCommandController {

    @Value("${spring.gateway.host}")
    private String host;
    @Value("${spring.gateway.port}")
    private String port;
    @Value("${spring.gateway.scheme}")
    private String scheme;

    private final OrderCommandService orderCommandService;

    public OrderCommandController(OrderCommandService orderCommandService) {
        this.orderCommandService = orderCommandService;
    }

    @PreAuthorize("hasAuthority('customers')")
    @PutMapping
    public ResponseEntity<?> createOrder(UriComponentsBuilder b, @Valid @RequestBody Order createOrder) {
        Order order = orderCommandService.createOrder(createOrder);

        UriComponents uriComponents = b.scheme(scheme).host(host).port(port).path("/orders/{orderId}").buildAndExpand(order.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PreAuthorize("hasAuthority('admins')")
    @PostMapping("/status")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody OrderStatusUpdateDTO statusUpdateDTO) {
        orderCommandService.updateStatus(statusUpdateDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/cancellation")
    public ResponseEntity<?> cancelOrder(@PathVariable long orderId) {
        orderCommandService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

}
