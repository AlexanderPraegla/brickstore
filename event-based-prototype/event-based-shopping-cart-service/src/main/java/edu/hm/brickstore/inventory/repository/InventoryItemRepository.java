package edu.hm.brickstore.inventory.repository;


import edu.hm.brickstore.inventory.entity.InventoryItem;
import edu.hm.brickstore.inventory.entity.InventoryItemStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InventoryItemRepository extends MongoRepository<InventoryItem, Long> {

    List<InventoryItem> findAllByNameContainingIgnoreCase(String name);

    List<InventoryItem> findAllByStatus(InventoryItemStatus status);
}
