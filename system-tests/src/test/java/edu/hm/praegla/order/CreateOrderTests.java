package edu.hm.praegla.order;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.account.dto.AddressDTO;
import edu.hm.praegla.account.dto.CustomerDTO;
import edu.hm.praegla.client.AccountTestTestClient;
import edu.hm.praegla.client.AwaitilityHelper;
import edu.hm.praegla.client.InventoryTestTestClient;
import edu.hm.praegla.client.OrderTestTestClient;
import edu.hm.praegla.client.ShoppingCartTestTestClient;
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

    private final InventoryTestTestClient inventoryTestClient;
    private final AccountTestTestClient accountTestClient;
    private final OrderTestTestClient orderTestClient;
    private final ShoppingCartTestTestClient shoppingCartTestClient;

    public CreateOrderTests() {
        inventoryTestClient = new InventoryTestTestClient(spec);
        orderTestClient = new OrderTestTestClient(spec);
        accountTestClient = new AccountTestTestClient(spec);
        shoppingCartTestClient = new ShoppingCartTestTestClient(spec);
    }

    @BeforeEach
    public void beforeEach(CustomerDTO customerDTO, AddressDTO addressDTO, InventoryItemDTO inventoryItemDTO) {
        testAccount = accountTestClient.createAccount(customerDTO, addressDTO);
    }

    @Test
    public void shouldCreateOrder(InventoryItemDTO inventoryItemOne, InventoryItemDTO inventoryItemTwo) {
        accountTestClient.creditAccount(testAccount.getId(), new BigDecimal("200.00"));

        inventoryItemOne.setPrice(new BigDecimal("29.99"));
        inventoryItemTwo.setPrice(new BigDecimal("19.99"));
        inventoryItemOne = inventoryTestClient.createInventoryItem(inventoryItemOne);
        inventoryItemTwo = inventoryTestClient.createInventoryItem(inventoryItemTwo);

        shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), inventoryItemOne.getId(), 2);
        shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), inventoryItemTwo.getId(), 3);
        ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO order = orderTestClient.createProcessedOrder(testAccount, shoppingCart);

        assertThat(order.getTotal()).isEqualTo(new BigDecimal("119.95"));
        assertThat(order.getStatus()).isEqualTo("PROCESSED");
        assertThat(order.getErrorCode()).isNullOrEmpty();

        AccountDTO account = accountTestClient.getAccountById(testAccount.getId());
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("80.05"));

        inventoryItemOne = inventoryTestClient.getInventoryItemById(inventoryItemOne.getId());
        inventoryItemTwo = inventoryTestClient.getInventoryItemById(inventoryItemTwo.getId());

        assertThat(inventoryItemOne.getStock()).isEqualTo(3);
        assertThat(inventoryItemTwo.getStock()).isEqualTo(2);
    }

    @Test
    public void shouldFailCreateOrderWithAccountWithEmptyShoppingCart() {
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setAccountId(testAccount.getId());
        shoppingCartDTO.setCustomerName("Testing");
        shoppingCartDTO.setLineItems(Collections.emptyList());
        ApiErrorDTO apiErrorDTO = orderTestClient.createOrderRequest(testAccount, shoppingCartDTO)
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
        accountTestClient.creditAccount(testAccount.getId(), new BigDecimal("200.00"));

        InventoryItemDTO inventoryItem = inventoryTestClient.createInventoryItem(inventoryItemDTO);
        shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), inventoryItem.getId(), orderQuantity);

        inventoryItem.setStock(stock);
        inventoryItem.setPrice(new BigDecimal("19.99"));
        inventoryItem.setStatus(inventoryStatus);
        inventoryTestClient.updateInventoryItem(inventoryItem);


        ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO order = orderTestClient.createOrder(testAccount, shoppingCart);

        long orderId = order.getId();
        AwaitilityHelper.wait(() -> {
            OrderDTO o = orderTestClient.getOrder(orderId);
            return StringUtils.isNotEmpty(o.getErrorCode());
        });

        order = orderTestClient.getOrder(order.getId());
        assertThat(order.getStatus()).isEqualTo("PAYED");
        assertThat(order.getErrorCode()).isEqualTo(responseCode);
    }

    @ParameterizedTest(name = "[{index}] Should fail with response code ''{0}'' for account balance={1} and status ''{2}''")
    @CsvSource({"'BALANCE_INSUFFICIENT', 10.00, 'ACTIVATED', 5",
            "'ACCOUNT_DEACTIVATED', 200.00, 'DEACTIVATED', 5"})
    public void shouldFailCreateOrderCausedByAccount(String responseCode, BigDecimal newBalance, String accountStatus, int orderQuantity, InventoryItemDTO inventoryItemDTO) {
        accountTestClient.creditAccount(testAccount.getId(), newBalance);

        inventoryItemDTO.setPrice(new BigDecimal("19.99"));
        InventoryItemDTO inventoryItem = inventoryTestClient.createInventoryItem(inventoryItemDTO);
        shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), inventoryItem.getId(), orderQuantity);

        accountTestClient.updateAccountStatus(testAccount.getId(), accountStatus);

        ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO order = orderTestClient.createOrder(testAccount, shoppingCart);

        long orderId = order.getId();
        AwaitilityHelper.wait(() -> {
            OrderDTO o = orderTestClient.getOrder(orderId);
            return StringUtils.isNotEmpty(o.getErrorCode());
        });

        order = orderTestClient.getOrder(order.getId());
        assertThat(order.getStatus()).isEqualTo("CREATED");
        assertThat(order.getErrorCode()).isEqualTo(responseCode);
    }

    @ParameterizedTest(name = "[{index}] Should fail with response code ''{0}'' for inventory item status ''{2}''")
    @CsvSource({"'NOT_ENOUGH_STOCK', 1, 'AVAILABLE', 5",
            "'OUT_OF_STOCK', 0, 'OUT_OF_STOCK', 2",
            "'ITEM_NOT_ORDERABLE', 10, 'DEACTIVATED', 3"})
    public void shouldFailCreateOrderWithTwoItemCausedByInventoryItem(String responseCode, int stock, String inventoryStatus, int orderQuantity, InventoryItemDTO inventoryItemDTO, InventoryItemDTO inventoryItemFailingDTO) {
        accountTestClient.creditAccount(testAccount.getId(), new BigDecimal("2000.00"));

        inventoryItemDTO.setPrice(new BigDecimal("20.99"));
        InventoryItemDTO inventoryItem = inventoryTestClient.createInventoryItem(inventoryItemDTO);
        inventoryItemFailingDTO.setPrice(new BigDecimal("45.99"));
        InventoryItemDTO inventoryItemFailing = inventoryTestClient.createInventoryItem(inventoryItemFailingDTO);
        shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), inventoryItem.getId(), orderQuantity)
                .then()
                .statusCode(200);
        shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), inventoryItemFailing.getId(), orderQuantity)
                .then()
                .statusCode(200);

        inventoryItemFailing.setStock(stock);
        inventoryItemFailing.setPrice(new BigDecimal("19.99"));
        inventoryItemFailing.setStatus(inventoryStatus);
        inventoryTestClient.updateInventoryItem(inventoryItemFailing);


        ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO order = orderTestClient.createOrder(testAccount, shoppingCart);

        long orderId = order.getId();
        AwaitilityHelper.wait(() -> {
            OrderDTO o = orderTestClient.getOrder(orderId);
            return StringUtils.isNotEmpty(o.getErrorCode());
        });

        order = orderTestClient.getOrder(order.getId());
        assertThat(order.getStatus()).isEqualTo("PAYED");
        assertThat(order.getErrorCode()).isEqualTo(responseCode);

        inventoryItem = inventoryTestClient.getInventoryItemById(inventoryItem.getId());
        inventoryItemFailing = inventoryTestClient.getInventoryItemById(inventoryItemFailing.getId());

        assertThat(inventoryItem.getStock()).isEqualTo(5);
        assertThat(inventoryItemFailing.getStock()).isEqualTo(stock);
    }
}
