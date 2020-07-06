package edu.hm.praegla.inventory.repository;


import edu.hm.praegla.inventory.entity.InventoryItem;
import edu.hm.praegla.inventory.entity.InventoryItemStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InventoryItemRepository extends CrudRepository<InventoryItem, Long> {

    List<InventoryItem> findAllByNameContainingIgnoreCase(String name);
    List<InventoryItem> findAllByStatus(InventoryItemStatus status);
}
