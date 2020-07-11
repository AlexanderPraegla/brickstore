package edu.hm.praegla.inventory.service;

import edu.hm.praegla.inventory.entity.InventoryItem;
import edu.hm.praegla.inventory.entity.InventoryItemStatus;
import edu.hm.praegla.inventory.repository.InventoryItemRepository;
import edu.hm.praegla.shoppingcart.error.EntityNotFoundException;
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
        return inventoryItemRepository.findAll();
    }

    public List<InventoryItem> getAvailableInventoryItems() {
        return inventoryItemRepository.findAllByStatus(InventoryItemStatus.AVAILABLE);
    }

    public List<InventoryItem> searchInventoryItems(String name) {
        return inventoryItemRepository.findAllByNameContainingIgnoreCase(name);
    }

    public InventoryItem getInventoryItem(long inventoryItemId) {
        return inventoryItemRepository.findById(inventoryItemId).orElseThrow(() -> new EntityNotFoundException(InventoryItem.class, "id", inventoryItemId));
    }
}
