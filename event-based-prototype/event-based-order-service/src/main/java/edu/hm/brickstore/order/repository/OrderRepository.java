package edu.hm.brickstore.order.repository;


import edu.hm.brickstore.order.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, Long> {

    Iterable<Order> findAllByAccountId(long accountId);
}
