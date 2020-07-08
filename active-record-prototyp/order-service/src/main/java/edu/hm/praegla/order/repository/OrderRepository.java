package edu.hm.praegla.order.repository;


import edu.hm.praegla.order.entity.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Optional<Order> findByAccountId(long accountId);
}
