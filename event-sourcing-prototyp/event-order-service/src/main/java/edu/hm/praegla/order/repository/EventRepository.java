package edu.hm.praegla.order.repository;

import edu.hm.praegla.order.event.Event;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event<?>, String> {

    List<Event<?>> findAllByAggregateId(long id, Sort sort);
}
