package edu.hm.praegla.shoppingcart.controller;

import edu.hm.praegla.shoppingcart.dto.AddShoppingCartItemDTO;
import edu.hm.praegla.shoppingcart.dto.ShoppingCartDTO;
import edu.hm.praegla.shoppingcart.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "shopping-carts", produces = {"application/json"})
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping
    public List<ShoppingCartDTO> getShoppingCarts() {
        return shoppingCartService.getShoppingCarts();
    }

    @GetMapping("/{accountId}")
    public ShoppingCartDTO getShoppingCart(@PathVariable long accountId) {
        return shoppingCartService.getShoppingCart(accountId);
    }

    @PutMapping
    public ResponseEntity<?> addShoppingCartItem(@Valid @RequestBody AddShoppingCartItemDTO addShoppingCartItemDTO) {
        shoppingCartService.addShoppingCartItem(addShoppingCartItemDTO.getAccountId(), addShoppingCartItemDTO.getInventoryItemId(), addShoppingCartItemDTO.getQuantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{accountId}/items/{lineItemId}")
    public ResponseEntity<?> removeShoppingCartItem(@PathVariable long accountId, @PathVariable long lineItemId) {
        shoppingCartService.removeShoppingCartItem(accountId, lineItemId);
        return ResponseEntity.ok().build();
    }

}
