package edu.hm.praegla.shoppingcart.controller;

import edu.hm.praegla.shoppingcart.dto.ShoppingCartDTO;
import edu.hm.praegla.shoppingcart.service.ShoppingCartQueryService;
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
@RequestMapping(value = "shopping-carts", produces = {"application/json"})
public class ShoppingCartQueryController {

    private final ShoppingCartQueryService shoppingCartQueryService;

    public ShoppingCartQueryController(ShoppingCartQueryService shoppingCartQueryService) {
        this.shoppingCartQueryService = shoppingCartQueryService;
    }

    @PreAuthorize("hasAuthority('admins')")
    @GetMapping
    public List<ShoppingCartDTO> getShoppingCarts() {
        return shoppingCartQueryService.getShoppingCarts();
    }

    @PreAuthorize("hasAuthority('customers')")
    @GetMapping("/{accountId}")
    public ShoppingCartDTO getShoppingCart(@PathVariable long accountId) {
        return shoppingCartQueryService.getShoppingCartDTO(accountId);
    }

}
