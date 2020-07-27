package edu.hm.brickstore.inventory.eventhandler;

import edu.hm.brickstore.inventory.entity.InventoryItem;
import edu.hm.brickstore.inventory.entity.InventoryItemStatus;
import edu.hm.brickstore.inventory.event.InventoryItemCreatedEvent;
import edu.hm.brickstore.inventory.event.InventoryItemGatheredEvent;
import edu.hm.brickstore.inventory.event.InventoryItemStatusUpdatedEvent;
import edu.hm.brickstore.inventory.event.InventoryItemStockedUpEvent;
import edu.hm.brickstore.inventory.event.InventoryItemUpdatedEvent;
import edu.hm.brickstore.inventory.repository.InventoryItemRepository;
import edu.hm.brickstore.inventory.service.InventoryQueryService;
import edu.hm.brickstore.messaging.config.MessagingRabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;

@Slf4j
@Component
@RabbitListener(queues = MessagingRabbitMqConfig.INVENTORY_QUEUE)
public class InventoryEventHandler {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryQueryService inventoryQueryService;

    public InventoryEventHandler(InventoryItemRepository inventoryItemRepository, InventoryQueryService inventoryQueryService) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryQueryService = inventoryQueryService;
    }

    @RabbitHandler
    public void processInventoryItemCreatedEvent(@Payload InventoryItemCreatedEvent event) {
        log.info("Received InventoryItemCreatedEvent: {}", event);
        inventoryItemRepository.save(event.getPayload());
    }

    @RabbitHandler
    public void processInventoryItemStatusUpdatedEvent(@Payload InventoryItemStatusUpdatedEvent event) {
        log.info("Received InventoryItemStatusUpdatedEvent: {}", event);
        InventoryItem inventoryItem = inventoryQueryService.getInventoryItem(event.getAggregateId());
        inventoryItem.setStatus(event.getPayload().getStatus());
        inventoryItemRepository.save(inventoryItem);
    }

    @RabbitHandler
    public void processInventoryItemUpdatedEvent(@Payload InventoryItemUpdatedEvent event) {
        log.info("Received InventoryItemUpdatedEvent: {}", event);
        InventoryItem updatedInventoryItem = inventoryQueryService.getInventoryItem(event.getAggregateId());
        InventoryItem inventoryItem = event.getPayload();
        updatedInventoryItem.setName(inventoryItem.getName());
        updatedInventoryItem.setPrice(inventoryItem.getPrice());
        updatedInventoryItem.setStock(inventoryItem.getStock());
        updatedInventoryItem.setDeliveryTime(inventoryItem.getDeliveryTime());
        updatedInventoryItem.setStatus(inventoryItem.getStatus());
        inventoryItemRepository.save(updatedInventoryItem);
    }

    @RabbitHandler
    public void processInventoryItemStockedUpEvent(@Payload InventoryItemStockedUpEvent event) {
        log.info("Received InventoryItemStockedUpEvent: {}", event);
        InventoryItem inventoryItem = inventoryQueryService.getInventoryItem(event.getAggregateId());
        @Min(0) int currentStock = inventoryItem.getStock();
        @Min(0) int quantity = event.getPayload().getQuantity();

        if (inventoryItem.getStatus() == InventoryItemStatus.OUT_OF_STOCK && +
                quantity > 0) {
            inventoryItem.setStatus(InventoryItemStatus.AVAILABLE);
        }

        inventoryItem.setStock(currentStock + quantity);
        inventoryItemRepository.save(inventoryItem);
    }

    @RabbitHandler
    public void processInventoryItemStockGatheredEvent(@Payload InventoryItemGatheredEvent event) {
        log.info("Received InventoryItemGatheredEvent: {}", event);
        InventoryItem inventoryItem = inventoryQueryService.getInventoryItem(event.getAggregateId());
        @Min(0) int currentStock = inventoryItem.getStock();
        @Min(0) int quantity = event.getPayload().getQuantity();
        inventoryItem.setStock(currentStock - quantity);

        if (inventoryItem.getStock() == 0) {
            inventoryItem.setStatus(InventoryItemStatus.OUT_OF_STOCK);
        }
        inventoryItemRepository.save(inventoryItem);
    }
}
