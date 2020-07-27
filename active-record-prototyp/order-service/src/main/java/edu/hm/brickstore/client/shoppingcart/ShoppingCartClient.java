package edu.hm.brickstore.client.shoppingcart;


import edu.hm.brickstore.client.shoppingcart.dto.ShoppingCartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Primary
@FeignClient(value = "shopping-cart-service", fallback = ShoppingCartClientFallBack.class)
public interface ShoppingCartClient {

    @RequestMapping(method = RequestMethod.GET, value = "/shopping-carts/{accountId}")
    ShoppingCartDTO getShoppingCart(@PathVariable long accountId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/shopping-carts/{accountId}")
    void deleteShoppingCart(@PathVariable long accountId);
}
