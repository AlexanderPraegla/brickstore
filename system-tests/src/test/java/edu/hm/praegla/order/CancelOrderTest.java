package edu.hm.praegla.order;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.account.dto.AddressDTO;
import edu.hm.praegla.account.dto.CustomerDTO;
import edu.hm.praegla.client.AccountClient;
import edu.hm.praegla.client.InventoryClient;
import edu.hm.praegla.client.OrderClient;
import edu.hm.praegla.client.ShoppingCartClient;
import edu.hm.praegla.error.dto.ApiErrorDTO;
import edu.hm.praegla.inventory.dto.InventoryItemDTO;
import edu.hm.praegla.order.dto.OrderDTO;
import edu.hm.praegla.parameterResolver.AddressParameterResolver;
import edu.hm.praegla.parameterResolver.CustomerParameterResolver;
import edu.hm.praegla.parameterResolver.InventoryItemParameterResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith({AddressParameterResolver.class, CustomerParameterResolver.class, InventoryItemParameterResolver.class})
public class CancelOrderTest extends BrickstoreRestTest {

    private final InventoryClient inventoryClient;
    private final AccountClient accountClient;
    private final OrderClient orderClient;
    private final ShoppingCartClient shoppingCartClient;

    public CancelOrderTest() {
        inventoryClient = new InventoryClient(spec);
        orderClient = new OrderClient(spec);
        accountClient = new AccountClient(spec);
        shoppingCartClient = new ShoppingCartClient(spec);
    }

    public static final int INITIAL_ITEM_STOCK = 5;
    public static final String INITIAL_ACCOUNT_BALANCE = "200.00";
    private AccountDTO testAccount;
    private InventoryItemDTO testInventoryItem;

    @BeforeEach
    public void beforeEach(CustomerDTO customerDTO, AddressDTO addressDTO, InventoryItemDTO inventoryItemDTO) {
        int orderQuantity = 5;

        testAccount = accountClient.createAccount(customerDTO, addressDTO);
        accountClient.chargeAccount(testAccount.getId(), new BigDecimal(INITIAL_ACCOUNT_BALANCE));

        inventoryItemDTO.setPrice(new BigDecimal("19.99"));
        testInventoryItem = inventoryClient.createInventoryItem(inventoryItemDTO);
        shoppingCartClient.addShoppingCartItem(testAccount.getId(), testInventoryItem.getId(), orderQuantity);
    }

    @Test
    public void shouldCancelOrderWithStatusCreated() {
        accountClient.debitAccount(testAccount.getId(), new BigDecimal("150.00"));


        OrderDTO testOrder = orderClient.createOrder(testAccount.getId());
        assertThat(testOrder.getStatus()).isEqualTo("CREATED");
        assertThat(testOrder.getErrorCode()).isEqualTo("BALANCE_INSUFFICIENT");

        InventoryItemDTO item = inventoryClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(item.getStock()).isEqualTo(INITIAL_ITEM_STOCK);

        orderClient.cancelOrder(testOrder.getId())
                .then()
                .statusCode(200);
        OrderDTO order = orderClient.getOrder(testOrder.getId());
        assertThat(order.getStatus()).isEqualTo("CANCELLATION_COMPLETED");

    }

    @Test
    public void shouldCancelOrderWithStatusPayed() {
        inventoryClient.gatherInventoryItem(testInventoryItem.getId(), 2);

        OrderDTO testOrder = orderClient.createOrder(testAccount.getId());
        testAccount = accountClient.getAccountById(testAccount.getId());
        InventoryItemDTO item = inventoryClient.getInventoryItemById(testInventoryItem.getId());

        assertThat(testOrder.getStatus()).isEqualTo("PAYED");
        assertThat(testOrder.getErrorCode()).isEqualTo("NOT_ENOUGH_STOCK");
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal("100.05"));
        assertThat(item.getStock()).isEqualTo(3);

        orderClient.cancelOrder(testOrder.getId())
                .then()
                .statusCode(200);
        OrderDTO order = orderClient.getOrder(testOrder.getId());
        assertThat(order.getStatus()).isEqualTo("CANCELLATION_COMPLETED");

        testAccount = accountClient.getAccountById(testAccount.getId());
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal(INITIAL_ACCOUNT_BALANCE));
    }

    @Test
    public void shouldCancelOrderWithStatusProcessed() {
        OrderDTO testOrder = orderClient.createOrder(testAccount.getId());
        assertThat(testOrder.getStatus()).isEqualTo("PROCESSED");

        testAccount = accountClient.getAccountById(testAccount.getId());
        InventoryItemDTO item = inventoryClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal("100.05"));
        assertThat(item.getStock()).isEqualTo(0);

        orderClient.cancelOrder(testOrder.getId())
                .then()
                .statusCode(200);
        OrderDTO order = orderClient.getOrder(testOrder.getId());
        assertThat(order.getStatus()).isEqualTo("CANCELLATION_COMPLETED");

        testAccount = accountClient.getAccountById(testAccount.getId());
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal(INITIAL_ACCOUNT_BALANCE));
        item = inventoryClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(item.getStock()).isEqualTo(INITIAL_ITEM_STOCK);
    }

    @Test
    public void shouldFailCancelOrderWithStatusShipped() {
        OrderDTO testOrder = orderClient.createOrder(testAccount.getId());
        orderClient.updateOrderStatus(testOrder.getId(), "SHIPPED");

        ApiErrorDTO apiErrorDTO = orderClient.cancelOrder(testOrder.getId())
                .then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ORDER_NOT_CANCELABLE");
    }

    @Test
    public void shouldFailCancelOrderWithStatusDelivered() {
        OrderDTO testOrder = orderClient.createOrder(testAccount.getId());
        orderClient.updateOrderStatus(testOrder.getId(), "SHIPPED");
        orderClient.updateOrderStatus(testOrder.getId(), "DELIVERED");

        ApiErrorDTO apiErrorDTO = orderClient.cancelOrder(testOrder.getId())
                .then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ORDER_NOT_CANCELABLE");
    }
}
