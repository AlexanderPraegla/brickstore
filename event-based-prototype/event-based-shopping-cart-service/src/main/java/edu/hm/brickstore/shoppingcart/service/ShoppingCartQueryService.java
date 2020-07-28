package edu.hm.brickstore.shoppingcart.service;

import edu.hm.brickstore.account.entity.Account;
import edu.hm.brickstore.account.service.AccountQueryService;
import edu.hm.brickstore.inventory.entity.InventoryItem;
import edu.hm.brickstore.inventory.service.InventoryQueryService;
import edu.hm.brickstore.shoppingcart.dto.LineItemDTO;
import edu.hm.brickstore.shoppingcart.dto.ShoppingCartDTO;
import edu.hm.brickstore.shoppingcart.entity.ShoppingCart;
import edu.hm.brickstore.shoppingcart.repository.ShoppingCartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional
public class ShoppingCartQueryService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final InventoryQueryService inventoryQueryService;
    private final AccountQueryService accountQueryService;
    private final SequenceGeneratorService sequenceGeneratorService;

    public ShoppingCartQueryService(ShoppingCartRepository shoppingCartRepository,
                                    InventoryQueryService inventoryQueryService,
                                    AccountQueryService accountQueryService, SequenceGeneratorService sequenceGeneratorService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.inventoryQueryService = inventoryQueryService;
        this.accountQueryService = accountQueryService;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    public List<ShoppingCartDTO> getShoppingCarts() {
        log.info("Get all shopping carts");
        Iterable<ShoppingCart> all = shoppingCartRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .map(this::getShoppingCartDTO)
                .collect(Collectors.toList());
    }

    public ShoppingCartDTO getShoppingCartDTO(long accountId) {
        ShoppingCart shoppingCart = getShoppingCart(accountId);
        return getShoppingCartDTO(shoppingCart);
    }

    public ShoppingCart getShoppingCart(long accountId) {
        log.info("Get shopping cart for accountId={}", accountId);
        Optional<ShoppingCart> shoppingCartOptional = shoppingCartRepository.findByAccountId(accountId);

        if (shoppingCartOptional.isPresent()) {
            return shoppingCartOptional.get();
        } else {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setId(sequenceGeneratorService.generateSequence(ShoppingCart.SEQUENCE_NAME));

            Account account = accountQueryService.getAccount(accountId);
            shoppingCart.setAccountId(accountId);
            shoppingCart.setCustomerName(account.getCustomerName());
            return shoppingCartRepository.save(shoppingCart);
        }
    }

    private ShoppingCartDTO getShoppingCartDTO(ShoppingCart shoppingCart) {
        Account account = accountQueryService.getAccount(shoppingCart.getAccountId());

        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setAccountId(account.getAccountId());
        shoppingCartDTO.setCustomerName(account.getCustomerName());

        List<LineItemDTO> lineItemDTOs = getLineItemDTOsOfShoppingCart(shoppingCart);
        shoppingCartDTO.setLineItems(lineItemDTOs);
        return shoppingCartDTO;
    }

    private List<LineItemDTO> getLineItemDTOsOfShoppingCart(ShoppingCart shoppingCart) {
        return Optional.ofNullable(shoppingCart.getLineItems())
                .orElse(new HashSet<>())
                .stream()
                .map(lineItem -> {
                    InventoryItem inventoryItem = inventoryQueryService.getInventoryItem(lineItem.getInventoryItemId());

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
