package edu.hm.brickstore.inventory.service;

import edu.hm.brickstore.error.EntityNotFoundException;
import edu.hm.brickstore.inventory.entity.InventoryItem;
import edu.hm.brickstore.inventory.repository.InventoryItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class InventoryQueryService {

    private final InventoryItemRepository inventoryItemRepository;

    public InventoryQueryService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public InventoryItem getInventoryItem(long inventoryItemId) {
        return inventoryItemRepository.findById(inventoryItemId).orElseThrow(() -> new EntityNotFoundException(InventoryItem.class, "id", inventoryItemId));
    }
}
