package edu.hm.praegla.inventory.service;

import edu.hm.praegla.error.EntityNotFoundException;
import edu.hm.praegla.inventory.entity.InventoryItem;
import edu.hm.praegla.inventory.entity.InventoryItemStatus;
import edu.hm.praegla.inventory.repository.InventoryItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
