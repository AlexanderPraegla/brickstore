package edu.hm.praegla.inventory.service;

import edu.hm.praegla.inventory.dto.UpdateInventoryItemStatusDTO;
import edu.hm.praegla.inventory.dto.UpdateInventoryItemsStockDTO;
import edu.hm.praegla.inventory.entity.InventoryItem;
import edu.hm.praegla.inventory.entity.InventoryItemStatus;
import edu.hm.praegla.inventory.error.ItemNotOrderableException;
import edu.hm.praegla.inventory.error.NotEnoughStockException;
import edu.hm.praegla.inventory.error.OutOfStockException;
import edu.hm.praegla.inventory.event.InventoryItemCreatedEvent;
import edu.hm.praegla.inventory.event.InventoryItemGatheredEvent;
import edu.hm.praegla.inventory.event.InventoryItemStatusUpdatedEvent;
import edu.hm.praegla.inventory.event.InventoryItemStockedUpEvent;
import edu.hm.praegla.inventory.event.InventoryItemUpdatedEvent;
import edu.hm.praegla.inventory.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.Min;

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
        long inventoryItemId = sequenceGenerator.generateSequence(InventoryItem.SEQUENCE_NAME);
        inventoryItem.setId(inventoryItemId);

        InventoryItemCreatedEvent event = new InventoryItemCreatedEvent(inventoryItemId, inventoryItem);
        eventRepository.save(event);

        messagingService.sendMessage(event);

        return inventoryItem;
    }

    public void updateInventoryItem(long inventoryItemId, InventoryItem inventoryItem) {
        InventoryItemUpdatedEvent event = new InventoryItemUpdatedEvent(inventoryItemId, inventoryItem);
        eventRepository.save(event);

        messagingService.sendMessage(event);
    }

    public void gatherInventoryItem(UpdateInventoryItemsStockDTO stockInventoryItemsDTOS) {
        stockInventoryItemsDTOS.getItems().forEach(this::gatherInventoryItem);
    }

    private void gatherInventoryItem(UpdateInventoryItemsStockDTO.Item item) {
        InventoryItem inventoryItem = inventoryQueryService.getInventoryItem(item.getInventoryItemId());
        @Min(0) int currentStock = inventoryItem.getStock();

        if (inventoryItem.getStatus() == InventoryItemStatus.OUT_OF_STOCK) {
            throw new OutOfStockException();
        } else if (inventoryItem.getStatus() == InventoryItemStatus.DEACTIVATED) {
            throw new ItemNotOrderableException();
        }

        if (currentStock < item.getQuantity()) {
            throw new NotEnoughStockException();
        }

        InventoryItemGatheredEvent event = new InventoryItemGatheredEvent(item.getInventoryItemId(), item);
        eventRepository.save(event);

        messagingService.sendMessage(event);
    }

    public void stockUpInventoryItem(UpdateInventoryItemsStockDTO stockInventoryItemsDTOS) {
        stockInventoryItemsDTOS.getItems().forEach(this::stockUpInventoryItem);
    }

    private void stockUpInventoryItem(UpdateInventoryItemsStockDTO.Item item) {
        long inventoryItemId = item.getInventoryItemId();
        InventoryItem inventoryItem = inventoryQueryService.getInventoryItem(inventoryItemId);

        InventoryItemStockedUpEvent event = new InventoryItemStockedUpEvent(inventoryItemId, item);
        eventRepository.save(event);

        messagingService.sendMessage(event);
    }

    public void updateStatus(long inventoryItemId, @Valid UpdateInventoryItemStatusDTO updateInventoryItemStatusDTO) {
        InventoryItem inventoryItem = inventoryQueryService.getInventoryItem(inventoryItemId);

        InventoryItemStatusUpdatedEvent event = new InventoryItemStatusUpdatedEvent(inventoryItemId, updateInventoryItemStatusDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event);
    }
}
