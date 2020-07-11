package edu.hm.praegla.inventory.repository;

import edu.hm.praegla.inventory.event.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event<?>, String> {

}
