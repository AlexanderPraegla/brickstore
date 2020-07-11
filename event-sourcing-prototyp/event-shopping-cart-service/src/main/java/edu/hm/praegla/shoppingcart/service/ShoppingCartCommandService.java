package edu.hm.praegla.shoppingcart.service;

import edu.hm.praegla.account.entity.Account;
import edu.hm.praegla.account.entity.AccountStatus;
import edu.hm.praegla.account.service.AccountQueryService;
import edu.hm.praegla.inventory.entity.InventoryItem;
import edu.hm.praegla.inventory.entity.InventoryItemStatus;
import edu.hm.praegla.inventory.service.InventoryQueryService;
import edu.hm.praegla.shoppingcart.dto.AddShoppingCartItemDTO;
import edu.hm.praegla.shoppingcart.dto.RemoveShoppingCartItemDTO;
import edu.hm.praegla.shoppingcart.entity.ShoppingCart;
import edu.hm.praegla.shoppingcart.error.AccountDeactivatedException;
import edu.hm.praegla.shoppingcart.error.ItemNotOrderableException;
import edu.hm.praegla.shoppingcart.error.OutOfStockException;
import edu.hm.praegla.shoppingcart.event.ItemAddedToShoppingCartEvent;
import edu.hm.praegla.shoppingcart.event.ItemRemovedFromShoppingCartEvent;
import edu.hm.praegla.shoppingcart.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional
public class ShoppingCartCommandService {

    private final EventRepository eventRepository;
    private final InventoryQueryService inventoryQueryService;
    private final AccountQueryService accountQueryService;
    private final ShoppingCartMessagingService shoppingCartMessagingService;
    private final ShoppingCartQueryService shoppingCartQueryService;

    public ShoppingCartCommandService(EventRepository eventRepository,
                                      InventoryQueryService inventoryQueryService,
                                      AccountQueryService accountQueryService, ShoppingCartMessagingService shoppingCartMessagingService,
                                      ShoppingCartQueryService shoppingCartQueryService) {
        this.eventRepository = eventRepository;
        this.inventoryQueryService = inventoryQueryService;
        this.accountQueryService = accountQueryService;
        this.shoppingCartMessagingService = shoppingCartMessagingService;
        this.shoppingCartQueryService = shoppingCartQueryService;
    }

    public void addShoppingCartItem(AddShoppingCartItemDTO addShoppingCartItemDTO) {
        long accountId = addShoppingCartItemDTO.getAccountId();
        Account account = accountQueryService.getAccount(accountId);
        ShoppingCart shoppingCart = shoppingCartQueryService.getShoppingCart(accountId);

        if (account.getStatus() == AccountStatus.DEACTIVATED) {
            throw new AccountDeactivatedException();
        }

        InventoryItem inventoryItem = inventoryQueryService.getInventoryItem(addShoppingCartItemDTO.getInventoryItemId());
        if (inventoryItem.getStatus() == InventoryItemStatus.DEACTIVATED) {
            throw new ItemNotOrderableException();
        }

        if (inventoryItem.getStatus() == InventoryItemStatus.OUT_OF_STOCK) {
            throw new OutOfStockException();
        }

        ItemAddedToShoppingCartEvent event = new ItemAddedToShoppingCartEvent(shoppingCart.getId(), addShoppingCartItemDTO);
        eventRepository.save(event);
        shoppingCartMessagingService.sendMessage(event);


    }

    public void removeShoppingCartItem(long accountId, long lineItemId) {
        ShoppingCart shoppingCart = shoppingCartQueryService.getShoppingCart(accountId);
        RemoveShoppingCartItemDTO removeShoppingCartItemDTO = new RemoveShoppingCartItemDTO(accountId, lineItemId);
        ItemRemovedFromShoppingCartEvent event = new ItemRemovedFromShoppingCartEvent(shoppingCart.getId(), removeShoppingCartItemDTO);
        eventRepository.save(event);
        shoppingCartMessagingService.sendMessage(event);
    }
}
