package edu.hm.brickstore.order.repository;


import edu.hm.brickstore.order.entity.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

    Iterable<Order> findAllByAccountId(long accountId, Sort sort);
}
