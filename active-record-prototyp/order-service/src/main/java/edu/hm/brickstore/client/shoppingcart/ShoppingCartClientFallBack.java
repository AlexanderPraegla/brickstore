package edu.hm.brickstore.client.shoppingcart;

import edu.hm.brickstore.client.shoppingcart.dto.ShoppingCartDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ShoppingCartClientFallBack implements ShoppingCartClient {

    public ShoppingCartDTO getShoppingCart(long accountId) {
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setAccountId(accountId);
        shoppingCartDTO.setLineItems(Collections.emptyList());
        return shoppingCartDTO;
    }

    @Override
    public void deleteShoppingCart(long accountId) {

    }
}
