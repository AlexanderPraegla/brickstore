package edu.hm.brickstore.inventory.service;

import edu.hm.brickstore.error.EntityNotFoundException;
import edu.hm.brickstore.error.ItemNotOrderableException;
import edu.hm.brickstore.error.NotEnoughStockException;
import edu.hm.brickstore.error.OutOfStockException;
import edu.hm.brickstore.inventory.dto.UpdateInventoryItemsStockDTO;
import edu.hm.brickstore.inventory.entity.InventoryItem;
import edu.hm.brickstore.inventory.entity.InventoryItemStatus;
import edu.hm.brickstore.inventory.repository.InventoryItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;

    public InventoryService(InventoryItemRepository inventoryItemRepository) {
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

    public InventoryItem createInventoryItem(InventoryItem inventoryItem) {
        log.info("Create new inventory item: {}", inventoryItem);
        return inventoryItemRepository.save(inventoryItem);
    }

    public void updateInventoryItem(long inventoryItemId, InventoryItem inventoryItem) {
        log.info("Update inventory item with inventoryItemId={}", inventoryItemId);
        InventoryItem i = getInventoryItem(inventoryItemId);
        i.setName(inventoryItem.getName());
        i.setPrice(inventoryItem.getPrice());
        i.setStock(inventoryItem.getStock());
        i.setDeliveryTime(inventoryItem.getDeliveryTime());
        i.setStatus(inventoryItem.getStatus());
        inventoryItemRepository.save(inventoryItem);
    }

    public void gatherInventoryItem(UpdateInventoryItemsStockDTO changeInventoryItemStockDTOS) {
        changeInventoryItemStockDTOS.getItems().forEach(item -> gatherInventoryItem(item.getInventoryItemId(), item.getQuantity()));
    }

    private void gatherInventoryItem(long inventoryItemId, int quantity) {
        log.info("Gather {} from inventory item with inventoryItemId={}", quantity, inventoryItemId);
        InventoryItem inventoryItem = getInventoryItem(inventoryItemId);
        @Min(0) int currentStock = inventoryItem.getStock();

        if (inventoryItem.getStatus() == InventoryItemStatus.OUT_OF_STOCK) {
            log.error("Inventory item with id={} is out of stock", inventoryItemId);
            throw new OutOfStockException();
        } else if (inventoryItem.getStatus() == InventoryItemStatus.DEACTIVATED) {
            log.error("Inventory item with id={} is deactivated", inventoryItemId);
            throw new ItemNotOrderableException();
        }

        if (currentStock < quantity) {
            log.info("Quantity for inventory item with id={} is to low. Requested quantity: '{}'. Stocked quantity: '{}'", inventoryItemId, currentStock, quantity);
            throw new NotEnoughStockException();
        }

        inventoryItem.setStock(currentStock - quantity);

        if (inventoryItem.getStock() == 0) {
            inventoryItem.setStatus(InventoryItemStatus.OUT_OF_STOCK);
        }
        inventoryItemRepository.save(inventoryItem);
    }

    public void stockUpInventoryItem(UpdateInventoryItemsStockDTO changeInventoryItemStockDTOS) {
        changeInventoryItemStockDTOS.getItems().forEach(item -> stockUpInventoryItem(item.getInventoryItemId(), item.getQuantity()));
    }

    private void stockUpInventoryItem(long inventoryItemId, int quantity) {
        log.info("Add {} to inventory item with inventoryItemId={}", quantity, inventoryItemId);
        InventoryItem inventoryItem = getInventoryItem(inventoryItemId);
        @Min(0) int currentStock = inventoryItem.getStock();

        if (inventoryItem.getStatus() == InventoryItemStatus.OUT_OF_STOCK) {
            inventoryItem.setStatus(InventoryItemStatus.AVAILABLE);
        }

        inventoryItem.setStock(currentStock + quantity);
        inventoryItemRepository.save(inventoryItem);
    }

    public void updateStatus(long inventoryItemId, InventoryItemStatus status) {
        log.info("Update status of inventory item with inventoryItemId={} to {}", inventoryItemId, status);
        Optional<InventoryItem> optionalInventory = inventoryItemRepository.findById(inventoryItemId);
        InventoryItem inventoryItem = optionalInventory.orElseThrow(() -> new EntityNotFoundException(InventoryItem.class, "id", inventoryItemId));
        inventoryItem.setStatus(status);
        inventoryItemRepository.save(inventoryItem);
    }
}
