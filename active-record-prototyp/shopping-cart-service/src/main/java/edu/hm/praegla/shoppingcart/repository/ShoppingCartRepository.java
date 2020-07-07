package edu.hm.praegla.shoppingcart.repository;


import edu.hm.praegla.shoppingcart.entity.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long> {

    Optional<ShoppingCart> findByAccountId(long accountId);
}
