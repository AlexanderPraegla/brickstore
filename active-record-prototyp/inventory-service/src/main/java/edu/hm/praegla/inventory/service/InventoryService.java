package edu.hm.praegla.inventory.service;

import edu.hm.praegla.inventory.entity.InventoryItem;
import edu.hm.praegla.inventory.entity.InventoryItemStatus;
import edu.hm.praegla.inventory.error.EntityNotFoundException;
import edu.hm.praegla.inventory.error.ItemNotOrderableException;
import edu.hm.praegla.inventory.error.NotEnoughStockException;
import edu.hm.praegla.inventory.error.OutOfStockException;
import edu.hm.praegla.inventory.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;

    public InventoryService(InventoryItemRepository inventoryItemRepository) {
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

    public InventoryItem createInventoryItem(InventoryItem inventoryItem) {
        return inventoryItemRepository.save(inventoryItem);
    }

    public void updateInventoryItem(long inventoryItemId, InventoryItem inventoryItem) {
        InventoryItem i = getInventoryItem(inventoryItemId);
        i.setName(inventoryItem.getName());
        i.setPrice(inventoryItem.getPrice());
        i.setStock(inventoryItem.getStock());
        i.setDeliveryTime(inventoryItem.getDeliveryTime());
        i.setStatus(inventoryItem.getStatus());
    }

    public void gatherInventoryItem(long inventoryItemId, int quantity) {
        InventoryItem inventoryItem = getInventoryItem(inventoryItemId);
        @Min(0) int currentStock = inventoryItem.getStock();

        if (inventoryItem.getStatus() == InventoryItemStatus.OUT_OF_STOCK) {
            throw new OutOfStockException();
        } else if (inventoryItem.getStatus() == InventoryItemStatus.DEACTIVATED) {
            throw new ItemNotOrderableException();
        }

        if (currentStock < quantity) {
            throw new NotEnoughStockException();
        }

        inventoryItem.setStock(currentStock - quantity);

        if (inventoryItem.getStock() == 0) {
            inventoryItem.setStatus(InventoryItemStatus.OUT_OF_STOCK);
        }
    }

    public void stockUpInventoryItem(long inventoryItemId, int quantity) {
        InventoryItem inventoryItem = getInventoryItem(inventoryItemId);
        @Min(0) int currentStock = inventoryItem.getStock();

        if (inventoryItem.getStatus() == InventoryItemStatus.OUT_OF_STOCK) {
            inventoryItem.setStatus(InventoryItemStatus.AVAILABLE);
        }

        inventoryItem.setStock(currentStock + quantity);

    }

    public void updateStatus(long inventoryItemId, InventoryItemStatus status) {
        Optional<InventoryItem> optionalInventory = inventoryItemRepository.findById(inventoryItemId);
        InventoryItem account = optionalInventory.orElseThrow(() -> new EntityNotFoundException(InventoryItem.class, "id", inventoryItemId));
        account.setStatus(status);
    }
}
