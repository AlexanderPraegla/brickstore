package edu.hm.praegla.order;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.account.dto.AddressDTO;
import edu.hm.praegla.account.dto.CustomerDTO;
import edu.hm.praegla.client.AccountClient;
import edu.hm.praegla.client.AwaitilityHelper;
import edu.hm.praegla.client.InventoryClient;
import edu.hm.praegla.client.OrderClient;
import edu.hm.praegla.client.ShoppingCartClient;
import edu.hm.praegla.error.dto.ApiErrorDTO;
import edu.hm.praegla.inventory.dto.InventoryItemDTO;
import edu.hm.praegla.order.dto.OrderDTO;
import edu.hm.praegla.parameterResolver.AddressParameterResolver;
import edu.hm.praegla.parameterResolver.CustomerParameterResolver;
import edu.hm.praegla.parameterResolver.InventoryItemParameterResolver;
import edu.hm.praegla.shoppingcart.dto.ShoppingCartDTO;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({AddressParameterResolver.class, CustomerParameterResolver.class, InventoryItemParameterResolver.class})
public class CreateOrderTests extends BrickstoreRestTest {

    private AccountDTO testAccount;

    private final InventoryClient inventoryClient;
    private final AccountClient accountClient;
    private final OrderClient orderClient;
    private final ShoppingCartClient shoppingCartClient;

    public CreateOrderTests() {
        inventoryClient = new InventoryClient(spec);
        orderClient = new OrderClient(spec);
        accountClient = new AccountClient(spec);
        shoppingCartClient = new ShoppingCartClient(spec);
    }

    @BeforeEach
    public void beforeEach(CustomerDTO customerDTO, AddressDTO addressDTO, InventoryItemDTO inventoryItemDTO) {
        testAccount = accountClient.createAccount(customerDTO, addressDTO);
    }

    @Test
    public void shouldCreateOrder(InventoryItemDTO inventoryItemOne, InventoryItemDTO inventoryItemTwo) {
        accountClient.chargeAccount(testAccount.getId(), new BigDecimal("200.00"));

        inventoryItemOne.setPrice(new BigDecimal("29.99"));
        inventoryItemTwo.setPrice(new BigDecimal("19.99"));
        inventoryItemOne = inventoryClient.createInventoryItem(inventoryItemOne);
        inventoryItemTwo = inventoryClient.createInventoryItem(inventoryItemTwo);

        shoppingCartClient.addShoppingCartItem(testAccount.getId(), inventoryItemOne.getId(), 2);
        shoppingCartClient.addShoppingCartItem(testAccount.getId(), inventoryItemTwo.getId(), 3);
        ShoppingCartDTO shoppingCart = shoppingCartClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO order = orderClient.createProcessedOrder(testAccount, shoppingCart);

        assertThat(order.getTotal()).isEqualTo(new BigDecimal("119.95"));
        assertThat(order.getStatus()).isEqualTo("PROCESSED");
        assertThat(order.getErrorCode()).isNullOrEmpty();

        AccountDTO account = accountClient.getAccountById(testAccount.getId());
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("80.05"));

        inventoryItemOne = inventoryClient.getInventoryItemById(inventoryItemOne.getId());
        inventoryItemTwo = inventoryClient.getInventoryItemById(inventoryItemTwo.getId());

        assertThat(inventoryItemOne.getStock()).isEqualTo(3);
        assertThat(inventoryItemTwo.getStock()).isEqualTo(2);
    }

    @Test
    public void shouldFailCreateOrderWithAccountWithEmptyShoppingCart() {
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setAccountId(testAccount.getId());
        shoppingCartDTO.setCustomerName("Testing");
        shoppingCartDTO.setLineItems(Collections.emptyList());
        ApiErrorDTO apiErrorDTO = orderClient.createOrderRequest(testAccount, shoppingCartDTO)
                .then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);
        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("NO_ITEMS_IN_SHOPPING_CART");
    }

    @ParameterizedTest(name = "[{index}] Should fail with response code ''{0}'' for inventory item status ''{2}''")
    @CsvSource({"'NOT_ENOUGH_STOCK', 1, 'AVAILABLE', 5",
            "'OUT_OF_STOCK', 0, 'OUT_OF_STOCK', 2",
            "'ITEM_NOT_ORDERABLE', 10, 'DEACTIVATED', 3"})
    public void shouldFailCreateOrderCausedByInventoryItem(String responseCode, int stock, String inventoryStatus, int orderQuantity, InventoryItemDTO inventoryItemDTO) {
        accountClient.chargeAccount(testAccount.getId(), new BigDecimal("200.00"));

        InventoryItemDTO inventoryItem = inventoryClient.createInventoryItem(inventoryItemDTO);
        shoppingCartClient.addShoppingCartItem(testAccount.getId(), inventoryItem.getId(), orderQuantity);

        inventoryItem.setStock(stock);
        inventoryItem.setPrice(new BigDecimal("19.99"));
        inventoryItem.setStatus(inventoryStatus);
        inventoryClient.updateInventoryItem(inventoryItem);


        ShoppingCartDTO shoppingCart = shoppingCartClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO order = orderClient.createOrder(testAccount, shoppingCart);

        long orderId = order.getId();
        AwaitilityHelper.wait(() -> {
            OrderDTO o = orderClient.getOrder(orderId);
            return StringUtils.isNotEmpty(o.getErrorCode());
        });

        order = orderClient.getOrder(order.getId());
        assertThat(order.getStatus()).isEqualTo("PAYED");
        assertThat(order.getErrorCode()).isEqualTo(responseCode);
    }

    @ParameterizedTest(name = "[{index}] Should fail with response code ''{0}'' for account balance={1} and status ''{2}''")
    @CsvSource({"'BALANCE_INSUFFICIENT', 10.00, 'ACTIVATED', 5",
            "'ACCOUNT_DEACTIVATED', 200.00, 'DEACTIVATED', 5"})
    public void shouldFailCreateOrderCausedByAccount(String responseCode, BigDecimal newBalance, String accountStatus, int orderQuantity, InventoryItemDTO inventoryItemDTO) {
        accountClient.chargeAccount(testAccount.getId(), newBalance);

        inventoryItemDTO.setPrice(new BigDecimal("19.99"));
        InventoryItemDTO inventoryItem = inventoryClient.createInventoryItem(inventoryItemDTO);
        shoppingCartClient.addShoppingCartItem(testAccount.getId(), inventoryItem.getId(), orderQuantity);

        accountClient.updateAccountStatus(testAccount.getId(), accountStatus);

        ShoppingCartDTO shoppingCart = shoppingCartClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO order = orderClient.createOrder(testAccount, shoppingCart);

        long orderId = order.getId();
        AwaitilityHelper.wait(() -> {
            OrderDTO o = orderClient.getOrder(orderId);
            return StringUtils.isNotEmpty(o.getErrorCode());
        });

        order = orderClient.getOrder(order.getId());
        assertThat(order.getStatus()).isEqualTo("CREATED");
        assertThat(order.getErrorCode()).isEqualTo(responseCode);
    }

    @ParameterizedTest(name = "[{index}] Should fail with response code ''{0}'' for inventory item status ''{2}''")
    @CsvSource({"'NOT_ENOUGH_STOCK', 1, 'AVAILABLE', 5",
            "'OUT_OF_STOCK', 0, 'OUT_OF_STOCK', 2",
            "'ITEM_NOT_ORDERABLE', 10, 'DEACTIVATED', 3"})
    public void shouldFailCreateOrderWithTwoItemCausedByInventoryItem(String responseCode, int stock, String inventoryStatus, int orderQuantity, InventoryItemDTO inventoryItemDTO, InventoryItemDTO inventoryItemFailingDTO) {
        accountClient.chargeAccount(testAccount.getId(), new BigDecimal("2000.00"));

        inventoryItemDTO.setPrice(new BigDecimal("20.99"));
        InventoryItemDTO inventoryItem = inventoryClient.createInventoryItem(inventoryItemDTO);
        inventoryItemFailingDTO.setPrice(new BigDecimal("45.99"));
        InventoryItemDTO inventoryItemFailing = inventoryClient.createInventoryItem(inventoryItemFailingDTO);
        shoppingCartClient.addShoppingCartItem(testAccount.getId(), inventoryItem.getId(), orderQuantity)
                .then()
                .statusCode(200);
        shoppingCartClient.addShoppingCartItem(testAccount.getId(), inventoryItemFailing.getId(), orderQuantity)
                .then()
                .statusCode(200);

        inventoryItemFailing.setStock(stock);
        inventoryItemFailing.setPrice(new BigDecimal("19.99"));
        inventoryItemFailing.setStatus(inventoryStatus);
        inventoryClient.updateInventoryItem(inventoryItemFailing);


        ShoppingCartDTO shoppingCart = shoppingCartClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO order = orderClient.createOrder(testAccount, shoppingCart);

        long orderId = order.getId();
        AwaitilityHelper.wait(() -> {
            OrderDTO o = orderClient.getOrder(orderId);
            return StringUtils.isNotEmpty(o.getErrorCode());
        });

        order = orderClient.getOrder(order.getId());
        assertThat(order.getStatus()).isEqualTo("PAYED");
        assertThat(order.getErrorCode()).isEqualTo(responseCode);

        inventoryItem = inventoryClient.getInventoryItemById(inventoryItem.getId());
        inventoryItemFailing = inventoryClient.getInventoryItemById(inventoryItemFailing.getId());

        assertThat(inventoryItem.getStock()).isEqualTo(5);
        assertThat(inventoryItemFailing.getStock()).isEqualTo(stock);
    }
}
