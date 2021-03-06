package edu.hm.brickstore.order;

import edu.hm.brickstore.BrickstoreRestTest;
import edu.hm.brickstore.account.dto.AccountDTO;
import edu.hm.brickstore.account.dto.AddressDTO;
import edu.hm.brickstore.account.dto.CustomerDTO;
import edu.hm.brickstore.client.AccountTestClient;
import edu.hm.brickstore.client.AwaitilityHelper;
import edu.hm.brickstore.client.InventoryTestClient;
import edu.hm.brickstore.client.OrderTestClient;
import edu.hm.brickstore.client.ShoppingCartTestClient;
import edu.hm.brickstore.error.dto.ApiErrorDTO;
import edu.hm.brickstore.inventory.dto.InventoryItemDTO;
import edu.hm.brickstore.order.dto.OrderDTO;
import edu.hm.brickstore.parameterResolver.AddressParameterResolver;
import edu.hm.brickstore.parameterResolver.CustomerParameterResolver;
import edu.hm.brickstore.parameterResolver.InventoryItemParameterResolver;
import edu.hm.brickstore.shoppingcart.dto.ShoppingCartDTO;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith({AddressParameterResolver.class, CustomerParameterResolver.class, InventoryItemParameterResolver.class})
public class CancelOrderTest extends BrickstoreRestTest {

    private final InventoryTestClient inventoryTestClient;
    private final AccountTestClient accountTestClient;
    private final OrderTestClient orderTestClient;
    private final ShoppingCartTestClient shoppingCartTestClient;

    public CancelOrderTest() {
        inventoryTestClient = new InventoryTestClient(spec);
        orderTestClient = new OrderTestClient(spec);
        accountTestClient = new AccountTestClient(spec);
        shoppingCartTestClient = new ShoppingCartTestClient(spec);
    }

    public static final int INITIAL_ITEM_STOCK = 5;
    public static final String INITIAL_ACCOUNT_BALANCE = "200.00";
    private AccountDTO testAccount;
    private InventoryItemDTO testInventoryItem;

    @BeforeEach
    public void beforeEach(CustomerDTO customerDTO, AddressDTO addressDTO, InventoryItemDTO inventoryItemDTO) {
        int orderQuantity = 5;

        testAccount = accountTestClient.createAccount(customerDTO, addressDTO);
        accountTestClient.creditAccount(testAccount.getId(), new BigDecimal(INITIAL_ACCOUNT_BALANCE));

        inventoryItemDTO.setPrice(new BigDecimal("19.99"));
        testInventoryItem = inventoryTestClient.createInventoryItem(inventoryItemDTO);
        shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), testInventoryItem.getId(), orderQuantity);
    }

    @Test
    public void shouldCancelOrderWithStatusCreated() {
        accountTestClient.debitAccount(testAccount.getId(), new BigDecimal("150.00"));

        ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO testOrder = orderTestClient.createOrderWithStatus(testAccount, shoppingCart, "CREATED");

        assertThat(testOrder.getStatus()).isEqualTo("CREATED");
        assertThat(testOrder.getErrorCode()).isEqualTo("BALANCE_INSUFFICIENT");

        InventoryItemDTO item = inventoryTestClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(item.getStock()).isEqualTo(INITIAL_ITEM_STOCK);

        orderTestClient.cancelOrder(testOrder.getId())
                .then()
                .statusCode(200);

        long orderId = testOrder.getId();
        AwaitilityHelper.wait(() -> {
            OrderDTO o = orderTestClient.getOrder(orderId);
            return StringUtils.equals(o.getStatus(), "CANCELLATION_COMPLETED");
        });

        OrderDTO order = orderTestClient.getOrder(testOrder.getId());
        assertThat(order.getStatus()).isEqualTo("CANCELLATION_COMPLETED");

    }

    @Test
    public void shouldCancelOrderWithStatusPayed() {
        inventoryTestClient.gatherInventoryItem(testInventoryItem.getId(), 2);

        ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO testOrder = orderTestClient.createOrderWithStatus(testAccount, shoppingCart, "PAYED");

        InventoryItemDTO item = inventoryTestClient.getInventoryItemById(testInventoryItem.getId());
        testAccount = accountTestClient.getAccountById(testAccount.getId());

        assertThat(testOrder.getStatus()).isEqualTo("PAYED");
        assertThat(testOrder.getErrorCode()).isEqualTo("NOT_ENOUGH_STOCK");
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal("100.05"));
        assertThat(item.getStock()).isEqualTo(3);

        orderTestClient.cancelOrder(testOrder.getId())
                .then()
                .statusCode(200);

        long orderId = testOrder.getId();
        AwaitilityHelper.wait(() -> {
            OrderDTO o = orderTestClient.getOrder(orderId);
            return StringUtils.equals(o.getStatus(), "CANCELLATION_COMPLETED");
        });

        OrderDTO order = orderTestClient.getOrder(testOrder.getId());
        assertThat(order.getStatus()).isEqualTo("CANCELLATION_COMPLETED");

        testAccount = accountTestClient.getAccountById(testAccount.getId());
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal(INITIAL_ACCOUNT_BALANCE));
    }

    @Test
    public void shouldCancelOrderWithStatusProcessed() {
        ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO testOrder = orderTestClient.createProcessedOrder(testAccount, shoppingCart);

        testAccount = accountTestClient.getAccountById(testAccount.getId());
        InventoryItemDTO item = inventoryTestClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal("100.05"));
        assertThat(item.getStock()).isEqualTo(0);

        orderTestClient.cancelOrder(testOrder.getId())
                .then()
                .statusCode(200);

        long orderId = testOrder.getId();
        AwaitilityHelper.wait(() -> {
            OrderDTO o = orderTestClient.getOrder(orderId);
            return StringUtils.equals(o.getStatus(), "CANCELLATION_COMPLETED");
        });

        OrderDTO order = orderTestClient.getOrder(testOrder.getId());
        assertThat(order.getStatus()).isEqualTo("CANCELLATION_COMPLETED");

        testAccount = accountTestClient.getAccountById(testAccount.getId());
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal(INITIAL_ACCOUNT_BALANCE));
        item = inventoryTestClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(item.getStock()).isEqualTo(INITIAL_ITEM_STOCK);
    }

    @Test
    public void shouldFailCancelOrderWithStatusShipped() {
        ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO testOrder = orderTestClient.createProcessedOrder(testAccount, shoppingCart);
        orderTestClient.updateOrderStatus(testOrder.getId(), "SHIPPED");

        ApiErrorDTO apiErrorDTO = orderTestClient.cancelOrder(testOrder.getId())
                .then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ORDER_NOT_CANCELABLE");
    }

    @Test
    public void shouldFailCancelOrderWithStatusDelivered() {
        ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
        OrderDTO testOrder = orderTestClient.createProcessedOrder(testAccount, shoppingCart);
        orderTestClient.updateOrderStatus(testOrder.getId(), "SHIPPED");
        orderTestClient.updateOrderStatus(testOrder.getId(), "DELIVERED");

        ApiErrorDTO apiErrorDTO = orderTestClient.cancelOrder(testOrder.getId())
                .then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ORDER_NOT_CANCELABLE");
    }
}
