package edu.hm.brickstore.inventory.eventhandler;

import edu.hm.brickstore.error.EntityNotFoundException;
import edu.hm.brickstore.inventory.entity.InventoryItem;
import edu.hm.brickstore.inventory.entity.InventoryItemStatus;
import edu.hm.brickstore.inventory.event.InventoryItemCreatedEvent;
import edu.hm.brickstore.inventory.event.InventoryItemGatheredEvent;
import edu.hm.brickstore.inventory.event.InventoryItemStatusUpdatedEvent;
import edu.hm.brickstore.inventory.event.InventoryItemStockedUpEvent;
import edu.hm.brickstore.inventory.event.InventoryItemUpdatedEvent;
import edu.hm.brickstore.inventory.repository.InventoryItemRepository;
import edu.hm.brickstore.messaging.config.MessagingRabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;

/**
 * Command event handler for all external events from the inventory-service concerning the order-service
 */
@Slf4j
@Component
@RabbitListener(queues = MessagingRabbitMqConfig.INVENTORY_TO_SHOPPING_CART_QUEUE)
public class InventoryCommandEventHandler {

    private final InventoryItemRepository inventoryItemRepository;

    public InventoryCommandEventHandler(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @RabbitHandler
    public void processInventoryItemCreatedEvent(@Payload InventoryItemCreatedEvent event) {
        log.info("Received InventoryItemCreatedEvent: {}", event);

        InventoryItem payload = event.getPayload();
        inventoryItemRepository.save(payload);
    }

    @RabbitHandler
    public void processInventoryItemStatusUpdatedEvent(@Payload InventoryItemStatusUpdatedEvent event) {
        log.info("Received InventoryItemStatusUpdatedEvent: {}", event);

        InventoryItem inventoryItem = getInventoryItem(event.getAggregateId());
        inventoryItem.setStatus(event.getPayload().getStatus());
        inventoryItemRepository.save(inventoryItem);
    }

    @RabbitHandler
    public void processInventoryItemUpdatedEvent(@Payload InventoryItemUpdatedEvent event) {
        log.info("Received InventoryItemUpdatedEvent: {}", event);

        InventoryItem inventoryItem = event.getPayload();
        inventoryItemRepository.save(inventoryItem);
    }

    @RabbitHandler
    public void processInventoryItemStockedUpEvent(@Payload InventoryItemStockedUpEvent event) {
        log.info("Received InventoryItemStockedUpEvent: {}", event);

        InventoryItem inventoryItem = getInventoryItem(event.getAggregateId());
        @Min(0) int currentStock = inventoryItem.getStock();
        @Min(0) int quantity = event.getPayload().getQuantity();

        if (inventoryItem.getStatus() == InventoryItemStatus.OUT_OF_STOCK) {
            inventoryItem.setStatus(InventoryItemStatus.AVAILABLE);
        }

        inventoryItem.setStock(currentStock + quantity);
        inventoryItemRepository.save(inventoryItem);
    }

    @RabbitHandler
    public void processInventoryItemStockGatheredEvent(@Payload InventoryItemGatheredEvent event) {
        log.info("Received InventoryItemGatheredEvent: {}", event);

        InventoryItem inventoryItem = getInventoryItem(event.getAggregateId());
        @Min(0) int currentStock = inventoryItem.getStock();
        @Min(0) int quantity = event.getPayload().getQuantity();
        inventoryItem.setStock(currentStock - quantity);

        if (inventoryItem.getStock() == 0) {
            inventoryItem.setStatus(InventoryItemStatus.OUT_OF_STOCK);
        }
        inventoryItemRepository.save(inventoryItem);
    }

    private InventoryItem getInventoryItem(long aggregateId) {
        return inventoryItemRepository.findById(aggregateId).orElseThrow(() -> new EntityNotFoundException(InventoryItem.class, "id", aggregateId));
    }
}
