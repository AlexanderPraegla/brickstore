package edu.hm.praegla.inventory.messaging;

import edu.hm.praegla.inventory.entity.InventoryItem;
import edu.hm.praegla.inventory.entity.InventoryItemStatus;
import edu.hm.praegla.inventory.event.InventoryItemCreatedEvent;
import edu.hm.praegla.inventory.event.InventoryItemGatheredEvent;
import edu.hm.praegla.inventory.event.InventoryItemStatusUpdatedEvent;
import edu.hm.praegla.inventory.event.InventoryItemStockedUpEvent;
import edu.hm.praegla.inventory.event.InventoryItemUpdatedEvent;
import edu.hm.praegla.inventory.repository.InventoryItemRepository;
import edu.hm.praegla.shoppingcart.error.EntityNotFoundException;
import edu.hm.praegla.shoppingcart.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;

@Slf4j
@Component
@RabbitListener(queues = InventoryMessagingConfig.INVENTORY_SERVICE_QUEUE)
public class InventoryMessageHandler {

    private final InventoryItemRepository inventoryItemRepository;
    private final EventRepository eventRepository;

    public InventoryMessageHandler(InventoryItemRepository inventoryItemRepository, EventRepository eventRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.eventRepository = eventRepository;
    }

    @RabbitHandler
    public void processInventoryItemCreatedEvent(@Payload InventoryItemCreatedEvent event) {
        log.info("Received InventoryItemCreatedEvent: {}", event);
        eventRepository.save(event);

        edu.hm.praegla.inventory.entity.InventoryItem payload = event.getPayload();
        inventoryItemRepository.save(payload);
    }

    @RabbitHandler
    public void processInventoryItemStatusUpdatedEvent(@Payload InventoryItemStatusUpdatedEvent event) {
        log.info("Received InventoryItemStatusUpdatedEvent: {}", event);
        eventRepository.save(event);

        InventoryItem inventoryItem = getInventoryItem(event.getAggregateId());
        inventoryItem.setStatus(event.getPayload().getStatus());
        inventoryItemRepository.save(inventoryItem);
    }

    @RabbitHandler
    public void processInventoryItemUpdatedEvent(@Payload InventoryItemUpdatedEvent event) {
        log.info("Received InventoryItemUpdatedEvent: {}", event);
        eventRepository.save(event);

        InventoryItem inventoryItem = event.getPayload();
        inventoryItemRepository.save(inventoryItem);
    }

    @RabbitHandler
    public void processInventoryItemStockedUpEvent(@Payload InventoryItemStockedUpEvent event) {
        log.info("Received InventoryItemStockedUpEvent: {}", event);
        eventRepository.save(event);

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
        eventRepository.save(event);

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
