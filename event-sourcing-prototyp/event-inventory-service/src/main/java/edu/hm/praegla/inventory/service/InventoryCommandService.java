package edu.hm.praegla.inventory.service;

import edu.hm.praegla.inventory.dto.UpdateInventoryItemStatusDTO;
import edu.hm.praegla.inventory.dto.UpdateInventoryItemsStockDTO;
import edu.hm.praegla.inventory.entity.InventoryItem;
import edu.hm.praegla.inventory.entity.InventoryItemStatus;
import edu.hm.praegla.inventory.error.ItemNotOrderableException;
import edu.hm.praegla.inventory.error.NotEnoughStockException;
import edu.hm.praegla.inventory.error.OutOfStockException;
import edu.hm.praegla.inventory.event.Event;
import edu.hm.praegla.inventory.event.InventoryItemCreatedEvent;
import edu.hm.praegla.inventory.event.InventoryItemGatheredEvent;
import edu.hm.praegla.inventory.event.InventoryItemStatusUpdatedEvent;
import edu.hm.praegla.inventory.event.InventoryItemStockedUpEvent;
import edu.hm.praegla.inventory.event.InventoryItemUpdatedEvent;
import edu.hm.praegla.inventory.repository.EventRepository;
import edu.hm.praegla.messaging.service.MessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class InventoryCommandService {

    private final EventRepository eventRepository;
    private final InventoryQueryService inventoryQueryService;
    private final SequenceGeneratorService sequenceGenerator;
    private final MessagingService messagingService;

    public InventoryCommandService(EventRepository eventRepository, InventoryQueryService inventoryQueryService, SequenceGeneratorService sequenceGenerator, MessagingService messagingService) {
        this.eventRepository = eventRepository;
        this.inventoryQueryService = inventoryQueryService;
        this.sequenceGenerator = sequenceGenerator;
        this.messagingService = messagingService;
    }

    public InventoryItem createInventoryItem(InventoryItem inventoryItem) {
        log.info("Create new inventory item: {}", inventoryItem);
        long inventoryItemId = sequenceGenerator.generateSequence(InventoryItem.SEQUENCE_NAME);
        inventoryItem.setId(inventoryItemId);

        InventoryItemCreatedEvent event = new InventoryItemCreatedEvent(inventoryItemId, inventoryItem);
        eventRepository.save(event);

        messagingService.sendMessage(event, "inventory.item.created");

        return inventoryItem;
    }

    public void updateInventoryItem(long inventoryItemId, InventoryItem inventoryItem) {
        InventoryItemUpdatedEvent event = new InventoryItemUpdatedEvent(inventoryItemId, inventoryItem);
        eventRepository.save(event);

        messagingService.sendMessage(event, "inventory.item.updated");
    }

    public void gatherInventoryItem(UpdateInventoryItemsStockDTO stockInventoryItemsDTOS) {
        List<Event<?>> events = stockInventoryItemsDTOS.getItems()
                .stream()
                .map(this::gatherInventoryItem)
                .collect(Collectors.toList());
        messagingService.sendMessages(events, "inventory.item.gathered");
    }

    private InventoryItemGatheredEvent gatherInventoryItem(UpdateInventoryItemsStockDTO.Item item) {
        log.info("Gather {} from inventory item with inventoryItemId={}", item.getQuantity(), item.getInventoryItemId());
        InventoryItem inventoryItem = inventoryQueryService.getInventoryItem(item.getInventoryItemId());
        @Min(0) int currentStock = inventoryItem.getStock();

        if (inventoryItem.getStatus() == InventoryItemStatus.OUT_OF_STOCK) {
            log.error("Inventory item with id={} is out of stock", item.getInventoryItemId());
            throw new OutOfStockException();
        } else if (inventoryItem.getStatus() == InventoryItemStatus.DEACTIVATED) {
            log.error("Inventory item with id={} is deactivated", item.getInventoryItemId());
            throw new ItemNotOrderableException();
        }

        if (currentStock < item.getQuantity()) {
            log.info("Quantity for inventory item with id={} is to low. Requested quantity: '{}'. Stocked quantity: '{}'", item.getInventoryItemId(), currentStock, item.getQuantity());
            throw new NotEnoughStockException();
        }

        InventoryItemGatheredEvent event = new InventoryItemGatheredEvent(item.getInventoryItemId(), item);
        eventRepository.save(event);
        return event;
    }

    public void stockUpInventoryItem(UpdateInventoryItemsStockDTO stockInventoryItemsDTOS) {
        List<Event<?>> events = stockInventoryItemsDTOS.getItems()
                .stream()
                .map(this::stockUpInventoryItem)
                .collect(Collectors.toList());
        messagingService.sendMessages(events, "inventory.item.stockedUp");
    }

    private InventoryItemStockedUpEvent stockUpInventoryItem(UpdateInventoryItemsStockDTO.Item item) {
        log.info("Add {} to inventory item with inventoryItemId={}", item.getQuantity(), item.getInventoryItemId());
        long inventoryItemId = item.getInventoryItemId();
        //Necessary for API error response if entity does not exist
        InventoryItem inventoryItem = inventoryQueryService.getInventoryItem(inventoryItemId);

        InventoryItemStockedUpEvent event = new InventoryItemStockedUpEvent(inventoryItemId, item);
        eventRepository.save(event);

        return event;
    }

    public void updateStatus(long inventoryItemId, @Valid UpdateInventoryItemStatusDTO updateInventoryItemStatusDTO) {
        log.info("Update status of inventory item with inventoryItemId={} to {}", inventoryItemId, updateInventoryItemStatusDTO.getStatus());
        InventoryItem inventoryItem = inventoryQueryService.getInventoryItem(inventoryItemId);

        InventoryItemStatusUpdatedEvent event = new InventoryItemStatusUpdatedEvent(inventoryItemId, updateInventoryItemStatusDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "inventory.status.updated");
    }

}
