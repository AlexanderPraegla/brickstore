package edu.hm.praegla.client.shoppingcart;

import edu.hm.praegla.client.shoppingcart.dto.ShoppingCartDTO;
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
