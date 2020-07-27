package edu.hm.brickstore.shoppingcart.repository;


import edu.hm.brickstore.shoppingcart.entity.LineItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LineItemRepository extends MongoRepository<LineItem, Long> {

}
