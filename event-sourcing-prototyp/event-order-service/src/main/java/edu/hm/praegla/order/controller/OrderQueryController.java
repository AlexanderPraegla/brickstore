package edu.hm.praegla.order.controller;

import edu.hm.praegla.order.entity.Order;
import edu.hm.praegla.order.service.OrderQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "orders", produces = {"application/json"})
@Tag(name = "Order query API")
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    public OrderQueryController(OrderQueryService orderQueryService) {
        this.orderQueryService = orderQueryService;
    }

    @PreAuthorize("hasAuthority('admins')")
    @GetMapping("open")
    public List<Order> getOpenOrders() {
        return orderQueryService.getOpenOrders();
    }

    @PreAuthorize("hasAuthority('customers')")
    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable long orderId) {
        return orderQueryService.getOrder(orderId);
    }

    @PreAuthorize("hasAuthority('customers')")
    @GetMapping("account/{accountId}")
    public Iterable<Order> getOrdersForAccount(@PathVariable long accountId) {
        return orderQueryService.getOrdersForAccount(accountId);
    }
}
