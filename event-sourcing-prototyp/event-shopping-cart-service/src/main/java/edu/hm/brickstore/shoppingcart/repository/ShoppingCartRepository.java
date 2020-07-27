package edu.hm.brickstore.shoppingcart.repository;


import edu.hm.brickstore.shoppingcart.entity.ShoppingCart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShoppingCartRepository extends MongoRepository<ShoppingCart, Long> {

    Optional<ShoppingCart> findByAccountId(long accountId);
}
