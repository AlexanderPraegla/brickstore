package edu.hm.praegla.order.service;

import edu.hm.praegla.messaging.service.MessagingService;
import edu.hm.praegla.order.dto.UpdateOrderStatusDTO;
import edu.hm.praegla.order.entity.Order;
import edu.hm.praegla.order.entity.OrderItem;
import edu.hm.praegla.order.error.NoItemsInShoppingCartException;
import edu.hm.praegla.order.event.OrderCanceledEvent;
import edu.hm.praegla.order.event.OrderCreatedEvent;
import edu.hm.praegla.order.event.OrderStatusUpdatedEvent;
import edu.hm.praegla.order.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderCommandService {

    private final OrderQueryService orderQueryService;
    private final EventRepository eventRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final MessagingService messagingService;

    public OrderCommandService(OrderQueryService orderQueryService, EventRepository eventRepository,
                               SequenceGeneratorService sequenceGeneratorService, MessagingService messagingService) {
        this.eventRepository = eventRepository;
        this.orderQueryService = orderQueryService;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.messagingService = messagingService;
    }


    public Order createOrder(Order order) {
        long orderId = sequenceGeneratorService.generateSequence(Order.SEQUENCE_NAME);
        order.setId(orderId);

        if (order.getOrderItems().size() == 0) {
            throw new NoItemsInShoppingCartException();
        }

        List<OrderItem> orderItems = order.getOrderItems()
                .stream()
                .peek(orderItem -> orderItem.setId(sequenceGeneratorService.generateSequence(OrderItem.SEQUENCE_NAME)))
                .collect(Collectors.toList());
        order.setOrderItems(orderItems);

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, order);
        eventRepository.save(event);
        messagingService.sendMessage(event, "order.created");
        return order;
    }

    public void updateStatus(UpdateOrderStatusDTO updateOrderStatusDTO) {
        OrderStatusUpdatedEvent event = new OrderStatusUpdatedEvent(updateOrderStatusDTO.getOrderId(), updateOrderStatusDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "order.status.updated");

    }

    public void cancelOrder(long orderId) {
        Order order = orderQueryService.getOrder(orderId);
        OrderCanceledEvent event = new OrderCanceledEvent(orderId, order);
        eventRepository.save(event);
        messagingService.sendMessage(event, "order.canceled");
    }

}
