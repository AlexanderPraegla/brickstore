package edu.hm.praegla.shoppingcart.controller;

import edu.hm.praegla.shoppingcart.dto.AddShoppingCartItemDTO;
import edu.hm.praegla.shoppingcart.service.ShoppingCartCommandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "shopping-carts", produces = {"application/json"})
@Tag(name = "Shopping cart command API")
public class ShoppingCartCommandController {

    private final ShoppingCartCommandService shoppingCartCommandService;

    public ShoppingCartCommandController(ShoppingCartCommandService shoppingCartCommandService) {
        this.shoppingCartCommandService = shoppingCartCommandService;
    }

    @PreAuthorize("hasAuthority('customers')")
    @PutMapping
    public ResponseEntity<?> addShoppingCartItem(@Valid @RequestBody AddShoppingCartItemDTO addShoppingCartItemDTO) {
        shoppingCartCommandService.addShoppingCartItem(addShoppingCartItemDTO);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('customers')")
    @DeleteMapping("/{accountId}/items/{lineItemId}")
    public ResponseEntity<?> removeShoppingCartItem(@PathVariable long accountId, @PathVariable long lineItemId) {
        shoppingCartCommandService.removeShoppingCartItem(accountId, lineItemId);
        return ResponseEntity.ok().build();
    }
}
