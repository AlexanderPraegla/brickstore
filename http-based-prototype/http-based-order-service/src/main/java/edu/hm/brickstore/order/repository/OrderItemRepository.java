package edu.hm.brickstore.order.repository;


import edu.hm.brickstore.order.entity.OrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

}
