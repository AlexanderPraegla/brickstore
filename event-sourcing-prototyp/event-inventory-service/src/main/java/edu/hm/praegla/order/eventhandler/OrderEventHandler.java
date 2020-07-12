package edu.hm.praegla.order.eventhandler;

import edu.hm.praegla.inventory.repository.InventoryItemRepository;
import edu.hm.praegla.inventory.service.InventoryCommandService;
import edu.hm.praegla.inventory.service.InventoryQueryService;
import edu.hm.praegla.messaging.config.MessagingRabbitMqConfig;
import edu.hm.praegla.order.event.OrderCanceledEvent;
import edu.hm.praegla.order.event.OrderPayedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues = MessagingRabbitMqConfig.ORDER_TO_INVENTORY_QUEUE)
public class OrderEventHandler {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryQueryService inventoryQueryService;
    private final InventoryCommandService inventoryCommandService;

    public OrderEventHandler(InventoryItemRepository inventoryItemRepository,
                             InventoryQueryService inventoryQueryService,
                             InventoryCommandService inventoryCommandService) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryQueryService = inventoryQueryService;
        this.inventoryCommandService = inventoryCommandService;
    }


    @RabbitHandler
    public void process(@Payload OrderPayedEvent event) {
        log.info("Received OrderPayedEvent: {}", event);

        inventoryCommandService.gatherInventoryItemsForOrder(event.getPayload());
    }

    @RabbitHandler
    public void process(@Payload OrderCanceledEvent event) {
        log.info("Received OrderCanceledEvent: {}", event);

    }
}
