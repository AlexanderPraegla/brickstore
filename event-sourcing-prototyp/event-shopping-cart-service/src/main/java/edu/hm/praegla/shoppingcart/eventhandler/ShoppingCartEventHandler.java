package edu.hm.praegla.shoppingcart.eventhandler;

import edu.hm.praegla.messaging.config.MessagingRabbitMqConfig;
import edu.hm.praegla.shoppingcart.dto.AddShoppingCartItemDTO;
import edu.hm.praegla.shoppingcart.dto.RemoveShoppingCartItemDTO;
import edu.hm.praegla.shoppingcart.entity.LineItem;
import edu.hm.praegla.shoppingcart.entity.ShoppingCart;
import edu.hm.praegla.shoppingcart.event.ItemAddedToShoppingCartEvent;
import edu.hm.praegla.shoppingcart.event.ItemRemovedFromShoppingCartEvent;
import edu.hm.praegla.shoppingcart.repository.ShoppingCartRepository;
import edu.hm.praegla.shoppingcart.service.SequenceGeneratorService;
import edu.hm.praegla.shoppingcart.service.ShoppingCartQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RabbitListener(queues = MessagingRabbitMqConfig.SHOPPING_CART_QUEUE)
public class ShoppingCartEventHandler {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartQueryService shoppingCartQueryService;
    private final SequenceGeneratorService sequenceGeneratorService;

    public ShoppingCartEventHandler(ShoppingCartRepository shoppingCartRepository, ShoppingCartQueryService shoppingCartQueryService, SequenceGeneratorService sequenceGeneratorService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartQueryService = shoppingCartQueryService;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @RabbitHandler
    public void process(ItemAddedToShoppingCartEvent event) {
        log.info("Received ItemAddedToShoppingCartEvent: {}", event);

        AddShoppingCartItemDTO payload = event.getPayload();
        long accountId = payload.getAccountId();
        long inventoryItemId = payload.getInventoryItemId();
        int quantity = payload.getQuantity();

        ShoppingCart shoppingCart = shoppingCartQueryService.getShoppingCart(accountId);
        LineItem lineItem = new LineItem(sequenceGeneratorService.generateSequence(LineItem.SEQUENCE_NAME), inventoryItemId, quantity);
        shoppingCart.addLineItem(lineItem);

        shoppingCartRepository.save(shoppingCart);
    }

    @RabbitHandler
    public void process(ItemRemovedFromShoppingCartEvent event) {
        log.info("Received ItemRemovedFromShoppingCartEvent: {}", event);

        RemoveShoppingCartItemDTO payload = event.getPayload();
        ShoppingCart shoppingCart = shoppingCartQueryService.getShoppingCart(payload.getAccountId());
        shoppingCart.removeLineItem(payload.getLineItemId());
        shoppingCartRepository.save(shoppingCart);
    }
}
