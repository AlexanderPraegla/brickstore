package edu.hm.praegla.order;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.account.AccountClient;
import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.error.dto.ApiErrorDTO;
import edu.hm.praegla.inventory.InventoryClient;
import edu.hm.praegla.inventory.dto.InventoryItemDTO;
import edu.hm.praegla.order.dto.OrderDTO;
import edu.hm.praegla.order.dto.OrderItemDTO;
import edu.hm.praegla.shoppingcart.ShoppingCartClient;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderTest extends BrickstoreRestTest {

    private final InventoryClient inventoryClient;
    private final AccountClient accountClient;
    private final ShoppingCartClient shoppingCartClient;
    private final OrderClient orderClient;

    public OrderTest() {
        inventoryClient = new InventoryClient(spec);
        orderClient = new OrderClient(spec);
        shoppingCartClient = new ShoppingCartClient(spec);
        accountClient = new AccountClient(spec);
    }

    @Test
    @Order(1)
    public void shouldGetAllOpen() {
        List<OrderDTO> accounts = orderClient.getOpenOrders();
        assertThat(accounts).hasSize(5);
    }

    @Test
    public void shouldCreateOrder() {
        long accountId = 16;
        int inventoryItemIdEiffelTower = 19;
        int inventoryItemIdNotreDame = 20;

        InventoryItemDTO inventoryItemEiffelTower = inventoryClient.getInventoryItemById(inventoryItemIdEiffelTower);
        InventoryItemDTO inventoryItemNotreDame = inventoryClient.getInventoryItemById(inventoryItemIdNotreDame);

        assertThat(inventoryItemEiffelTower.getStock()).isEqualTo(4);
        assertThat(inventoryItemNotreDame.getStock()).isEqualTo(4);

        String createdOrderLocation = orderClient.createOrder(accountId)
                .then()
                .statusCode(201)
                .extract().header("location");
        OrderDTO order = orderClient.getResourceByLocationHeader(createdOrderLocation, OrderDTO.class);

        assertThat(order.getTotal()).isEqualTo(new BigDecimal("119.95"));

        AccountDTO account = accountClient.getAccountById(accountId);
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("80.05"));

        inventoryItemEiffelTower = inventoryClient.getInventoryItemById(inventoryItemIdEiffelTower);
        inventoryItemNotreDame = inventoryClient.getInventoryItemById(inventoryItemIdNotreDame);

        assertThat(inventoryItemEiffelTower.getStock()).isEqualTo(2);
        assertThat(inventoryItemNotreDame.getStock()).isEqualTo(1);
    }

    @Test
    public void shouldFailCreateOrderWithAccountWithEmptyShoppingCart() {

    }

    @Test
    public void shouldNotChangeOrderTotalWhenInventoryItemPriceIsChanged() {
        int inventoryItemIdBrandenburgerGate = 21;
        long orderId = 1;

        InventoryItemDTO inventoryItem = inventoryClient.getInventoryItemById(inventoryItemIdBrandenburgerGate);
        inventoryItem.setPrice(new BigDecimal("109.99"));
        inventoryClient.updateInventoryItem(inventoryItem)
                .then()
                .statusCode(200);

        OrderDTO order = orderClient.getOrder(orderId);
        assertThat(order.getTotal()).isEqualTo(new BigDecimal("49.99"));

        OrderItemDTO orderItemDTO = order.getOrderItems().get(0);
        assertThat(orderItemDTO.getInventoryItemId()).isEqualTo(inventoryItemIdBrandenburgerGate);
        assertThat(orderItemDTO.getPrice()).isEqualTo(new BigDecimal("49.99"));
    }

    @ParameterizedTest(name = "[{index}] Creating order for accountId={0} should return ''{1}''")
    @CsvSource({"18, 'NOT_ENOUGH_STOCK'",
            "19, 'OUT_OF_STOCK'",
            "20, 'ITEM_NOT_ORDERABLE'",
            "21, 'BALANCE_INSUFFICIENT'",
            "22, 'ACCOUNT_INACTIVE'"})
    public void shouldFailCreateOrder(long accountId, String responseCode) {
        ApiErrorDTO apiErrorDTO = orderClient.createOrder(accountId)
                .then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo(responseCode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PROCESSED", "SHIPPED", "DELIVERED"})
    public void shouldChangeOrderStatus(String status) {
        long orderId = 2;
        orderClient.updateOrderStatus(orderId, status)
                .then()
                .statusCode(200);
        OrderDTO order = orderClient.getOrder(orderId);
        assertThat(order.getStatus()).isEqualTo(status);
    }

    @ParameterizedTest(name = "[{index}] Cancel order with status {0}")
    @CsvSource({
            "CREATED, 3, 117.65, 27, 5",
            "PAYED, 4, 62.00, 27, 5",
            "PROCESSED, 5, 73.98, 27, 8"
    })
    public void shouldCancelOrder(String status, long orderId, BigDecimal resultingAccountBalance, long inventoryItemId, int resultingInventoryItemStock) {
        orderClient.cancelOrder(orderId)
                .then()
                .statusCode(200);
        OrderDTO order = orderClient.getOrder(orderId);
        assertThat(order.getStatus()).isEqualTo("CANCELLATION_COMPLETED");

        AccountDTO account = accountClient.getAccountById(order.getAccountId());
        assertThat(account.getBalance()).isEqualTo(resultingAccountBalance);

        InventoryItemDTO inventoryItem = inventoryClient.getInventoryItemById(inventoryItemId);
        assertThat(inventoryItem.getStock()).isEqualTo(resultingInventoryItemStock);
    }

}
