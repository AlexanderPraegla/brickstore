package edu.hm.praegla.shoppingcart.repository;


import edu.hm.praegla.shoppingcart.entity.LineItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LineItemRepository extends MongoRepository<LineItem, Long> {

}
