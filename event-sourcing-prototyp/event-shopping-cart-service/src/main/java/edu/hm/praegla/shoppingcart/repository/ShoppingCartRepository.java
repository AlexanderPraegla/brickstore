package edu.hm.praegla.shoppingcart.repository;


import edu.hm.praegla.shoppingcart.entity.ShoppingCart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShoppingCartRepository extends MongoRepository<ShoppingCart, Long> {

    Optional<ShoppingCart> findByAccountId(long accountId);
}
