package edu.hm.praegla.order.repository;


import edu.hm.praegla.order.entity.OrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

}
