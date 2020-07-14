package edu.hm.praegla.shoppingcart.service;

import edu.hm.praegla.client.account.AccountClient;
import edu.hm.praegla.client.account.dto.AccountDTO;
import edu.hm.praegla.client.account.dto.AccountStatus;
import edu.hm.praegla.client.inventory.InventoryClient;
import edu.hm.praegla.client.inventory.dto.InventoryItemDTO;
import edu.hm.praegla.client.inventory.dto.InventoryItemStatus;
import edu.hm.praegla.error.AccountDeactivatedException;
import edu.hm.praegla.error.EntityNotFoundException;
import edu.hm.praegla.error.ItemNotOrderableException;
import edu.hm.praegla.error.OutOfStockException;
import edu.hm.praegla.shoppingcart.dto.LineItemDTO;
import edu.hm.praegla.shoppingcart.dto.ShoppingCartDTO;
import edu.hm.praegla.shoppingcart.entity.LineItem;
import edu.hm.praegla.shoppingcart.entity.ShoppingCart;
import edu.hm.praegla.shoppingcart.repository.LineItemRepository;
import edu.hm.praegla.shoppingcart.repository.ShoppingCartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final LineItemRepository lineItemRepository;
    private final InventoryClient inventoryClient;
    private final AccountClient accountClient;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, LineItemRepository lineItemRepository, InventoryClient inventoryClient, AccountClient accountClient) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.lineItemRepository = lineItemRepository;
        this.inventoryClient = inventoryClient;
        this.accountClient = accountClient;
    }

    public List<ShoppingCartDTO> getShoppingCarts() {
        log.info("Get all shopping carts");
        Iterable<ShoppingCart> all = shoppingCartRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .map(this::getShoppingCartDTO)
                .collect(Collectors.toList());
    }

    public ShoppingCartDTO getShoppingCart(long accountId) {
        log.info("Get shopping cart for accountId={}", accountId);
        Optional<ShoppingCart> shoppingCartOptional = shoppingCartRepository.findByAccountId(accountId);
        if (shoppingCartOptional.isPresent()) {
            return getShoppingCartDTO(shoppingCartOptional.get());
        } else {
            throw new EntityNotFoundException(ShoppingCart.class, "account", accountId);
        }
    }

    public void addShoppingCartItem(long accountId, long inventoryItemId, int quantity) {
        log.info("Add {} of inventoryItemId={} to shopping cart of account={}", quantity, inventoryItemId, accountId);
        AccountDTO account = accountClient.getAccount(accountId);
        if (account.getStatus() == AccountStatus.DEACTIVATED) {
            throw new AccountDeactivatedException();
        }

        InventoryItemDTO inventoryItem = inventoryClient.getInventoryItem(inventoryItemId);
        if (inventoryItem.getStatus() == InventoryItemStatus.DEACTIVATED) {
            throw new ItemNotOrderableException();
        }

        if (inventoryItem.getStatus() == InventoryItemStatus.OUT_OF_STOCK) {
            throw new OutOfStockException();
        }

        ShoppingCart shoppingCart = shoppingCartRepository.findByAccountId(accountId).orElse(new ShoppingCart());
        shoppingCart.setAccountId(accountId);
        LineItem lineItem = new LineItem(inventoryItemId, quantity);
        lineItem.setShoppingCart(shoppingCart);

        lineItemRepository.save(lineItem);
    }

    public void removeShoppingCartItem(long accountId, long lineItemId) {
        log.info("Remove lineItemId={} from shopping cart of accountId={}", lineItemId, accountId);
        Optional<LineItem> lineItemOptional = lineItemRepository.findByIdAndAccountId(lineItemId, accountId);
        if (lineItemOptional.isPresent()) {
            LineItem lineItem = lineItemOptional.get();
            lineItemRepository.delete(lineItem);
        } else {
            throw new EntityNotFoundException(String.format("Line item with id=%d not found in shopping card of account id=%d", lineItemId, accountId));
        }
    }


    private ShoppingCartDTO getShoppingCartDTO(ShoppingCart shoppingCart) {
        AccountDTO account = accountClient.getAccount(shoppingCart.getAccountId());

        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setAccountId(account.getId());
        shoppingCartDTO.setCustomerName(account.getCustomer().getFirstname() + " " + account.getCustomer().getLastname());

        List<LineItemDTO> lineItemDTOs = getLineItemDTOsOfShoppingCart(shoppingCart);
        shoppingCartDTO.setLineItems(lineItemDTOs);
        return shoppingCartDTO;
    }

    private List<LineItemDTO> getLineItemDTOsOfShoppingCart(ShoppingCart shoppingCart) {
        return shoppingCart.getLineItems()
                .stream()
                .map(lineItem -> {
                    InventoryItemDTO inventoryItem = inventoryClient.getInventoryItem(lineItem.getInventoryItemId());

                    LineItemDTO lineItemDTO = new LineItemDTO();
                    lineItemDTO.setLineItemId(lineItem.getId());
                    lineItemDTO.setInventoryItemId(lineItem.getInventoryItemId());
                    lineItemDTO.setQuantity(lineItem.getQuantity());
                    lineItemDTO.setName(inventoryItem.getName());
                    lineItemDTO.setPrice(inventoryItem.getPrice());
                    lineItemDTO.setDeliveryTime(inventoryItem.getDeliveryTime());
                    return lineItemDTO;
                }).collect(Collectors.toList());
    }
}
