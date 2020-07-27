package edu.hm.brickstore.order.eventhandler;

import edu.hm.brickstore.error.BrickstoreException;
import edu.hm.brickstore.inventory.dto.UpdateInventoryItemsStockDTO;
import edu.hm.brickstore.inventory.event.Event;
import edu.hm.brickstore.inventory.service.InventoryCommandService;
import edu.hm.brickstore.messaging.config.MessagingRabbitMqConfig;
import edu.hm.brickstore.messaging.service.MessagingService;
import edu.hm.brickstore.order.dto.OrderErrorDTO;
import edu.hm.brickstore.order.dto.OrderStatusUpdateDTO;
import edu.hm.brickstore.order.entity.Order;
import edu.hm.brickstore.order.entity.OrderStatus;
import edu.hm.brickstore.order.event.OrderGatherInventoryItemFailedEvent;
import edu.hm.brickstore.order.event.OrderGatherInventoryItemSucceededEvent;
import edu.hm.brickstore.order.event.OrderPayedEvent;
import edu.hm.brickstore.order.event.OrderRefundedEvent;
import edu.hm.brickstore.order.event.OrderReturnInventoryItemFailedEvent;
import edu.hm.brickstore.order.event.OrderReturnInventoryItemSucceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RabbitListener(queues = MessagingRabbitMqConfig.ORDER_TO_INVENTORY_QUEUE)
public class OrderEventHandler {

    private final InventoryCommandService inventoryCommandService;
    private final MessagingService messagingService;

    public OrderEventHandler(InventoryCommandService inventoryCommandService, MessagingService messagingService) {
        this.inventoryCommandService = inventoryCommandService;
        this.messagingService = messagingService;
    }

    @RabbitHandler
    public void process(@Payload OrderPayedEvent event) {
        log.info("Received OrderPayedEvent: {}", event);
        Order order = event.getPayload();

        Event<?> result;
        try {
            List<UpdateInventoryItemsStockDTO.Item> items = toUpdateItemStockDTOItems(order);

            inventoryCommandService.gatherInventoryItem(new UpdateInventoryItemsStockDTO(items));
            OrderStatusUpdateDTO orderStatusUpdateDTO = new OrderStatusUpdateDTO(order.getId(), OrderStatus.PROCESSED);
            result = new OrderGatherInventoryItemSucceededEvent(order.getId(), orderStatusUpdateDTO);
            messagingService.sendMessage(result, "order.item.gather.succeeded");
        } catch (BrickstoreException e) {
            log.error("Gather inventory items for orderId={} failed", order.getId(), e);
            OrderErrorDTO orderErrorDTO = new OrderErrorDTO(order.getId(), e.getResponseCode());
            result = new OrderGatherInventoryItemFailedEvent(order.getId(), orderErrorDTO);
            messagingService.sendMessage(result, "order.item.gather.failed");
        }
    }

    @RabbitHandler
    public void process(@Payload OrderRefundedEvent event) {
        log.info("Received OrderRefundedEvent: {}", event);
        Order order = event.getPayload();

        Event<?> result;
        try {
            List<UpdateInventoryItemsStockDTO.Item> items = toUpdateItemStockDTOItems(order);

            inventoryCommandService.stockUpInventoryItem(new UpdateInventoryItemsStockDTO(items));
            OrderStatusUpdateDTO orderStatusUpdateDTO = new OrderStatusUpdateDTO(order.getId(), OrderStatus.PROCESSED);
            result = new OrderReturnInventoryItemSucceededEvent(order.getId(), orderStatusUpdateDTO);
            messagingService.sendMessage(result, "order.item.return.succeeded");
        } catch (BrickstoreException e) {
            log.error("Return inventory items for orderId={} failed", order.getId(), e);
            OrderErrorDTO orderErrorDTO = new OrderErrorDTO(order.getId(), e.getResponseCode());
            result = new OrderReturnInventoryItemFailedEvent(order.getId(), orderErrorDTO);
            messagingService.sendMessage(result, "order.item.return.failed");
        }
    }

    private List<UpdateInventoryItemsStockDTO.Item> toUpdateItemStockDTOItems(Order order) {
        return order.getOrderItems()
                .stream()
                .map(orderItem -> {
                    UpdateInventoryItemsStockDTO.Item item = new UpdateInventoryItemsStockDTO.Item();
                    item.setInventoryItemId(orderItem.getInventoryItemId());
                    item.setQuantity(orderItem.getQuantity());
                    return item;
                })
                .collect(Collectors.toList());
    }
}
