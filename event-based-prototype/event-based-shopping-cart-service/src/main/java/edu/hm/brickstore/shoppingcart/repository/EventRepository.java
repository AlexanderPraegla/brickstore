package edu.hm.brickstore.shoppingcart.repository;

import edu.hm.brickstore.shoppingcart.event.Event;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event<?>, String> {

    List<Event<?>> findAllByAggregateId(long id, Sort sort);
}
