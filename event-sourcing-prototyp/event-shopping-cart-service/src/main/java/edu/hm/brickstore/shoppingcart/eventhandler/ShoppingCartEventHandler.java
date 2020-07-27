package edu.hm.brickstore.shoppingcart.eventhandler;

import edu.hm.brickstore.messaging.config.MessagingRabbitMqConfig;
import edu.hm.brickstore.shoppingcart.dto.AddShoppingCartItemDTO;
import edu.hm.brickstore.shoppingcart.dto.RemoveShoppingCartItemDTO;
import edu.hm.brickstore.shoppingcart.entity.LineItem;
import edu.hm.brickstore.shoppingcart.entity.ShoppingCart;
import edu.hm.brickstore.shoppingcart.event.ItemAddedToShoppingCartEvent;
import edu.hm.brickstore.shoppingcart.event.ItemRemovedFromShoppingCartEvent;
import edu.hm.brickstore.shoppingcart.repository.ShoppingCartRepository;
import edu.hm.brickstore.shoppingcart.service.SequenceGeneratorService;
import edu.hm.brickstore.shoppingcart.service.ShoppingCartQueryService;
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
