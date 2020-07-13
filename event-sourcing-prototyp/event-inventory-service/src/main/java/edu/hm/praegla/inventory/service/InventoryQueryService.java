package edu.hm.praegla.inventory.service;

import edu.hm.praegla.inventory.entity.InventoryItem;
import edu.hm.praegla.inventory.entity.InventoryItemStatus;
import edu.hm.praegla.inventory.error.EntityNotFoundException;
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

    public Iterable<InventoryItem> getInventoryItems() {
        log.info("Get all inventory items");
        return inventoryItemRepository.findAll();
    }

    public List<InventoryItem> getAvailableInventoryItems() {
        log.info("Get all inventory items with status AVAILABLE");
        return inventoryItemRepository.findAllByStatus(InventoryItemStatus.AVAILABLE);
    }

    public List<InventoryItem> searchInventoryItems(String name) {
        log.info("Search for inventory items with name={}", name);
        return inventoryItemRepository.findAllByNameContainingIgnoreCase(name);
    }

    public InventoryItem getInventoryItem(long inventoryItemId) {
        log.info("Get inventory item for inventoryItemId={}", inventoryItemId);
        return inventoryItemRepository.findById(inventoryItemId).orElseThrow(() -> new EntityNotFoundException(InventoryItem.class, "id", inventoryItemId));
    }
}
