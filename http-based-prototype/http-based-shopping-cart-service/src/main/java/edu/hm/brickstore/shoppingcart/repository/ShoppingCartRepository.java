package edu.hm.brickstore.shoppingcart.repository;


import edu.hm.brickstore.shoppingcart.entity.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long> {

    Optional<ShoppingCart> findByAccountId(long accountId);
}
