package edu.hm.brickstore.error;

import org.springframework.http.HttpStatus;

public class NoItemsInShoppingCartException extends BrickstoreException {

    public NoItemsInShoppingCartException() {
        super(ResponseCode.NO_ITEMS_IN_SHOPPING_CART, HttpStatus.BAD_REQUEST, "There are no item in the shopping cart");
    }
}
