package edu.hm.brickstore.inventory.repository;

import edu.hm.brickstore.inventory.event.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event<?>, String> {

}
