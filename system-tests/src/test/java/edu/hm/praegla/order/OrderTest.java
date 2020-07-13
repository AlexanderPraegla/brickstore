package edu.hm.praegla.order;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.account.dto.AddressDTO;
import edu.hm.praegla.account.dto.CustomerDTO;
import edu.hm.praegla.client.AccountClient;
import edu.hm.praegla.client.InventoryClient;
import edu.hm.praegla.client.OrderClient;
import edu.hm.praegla.client.ShoppingCartClient;
import edu.hm.praegla.inventory.dto.InventoryItemDTO;
import edu.hm.praegla.order.dto.OrderDTO;
import edu.hm.praegla.order.dto.OrderItemDTO;
import edu.hm.praegla.parameterResolver.AddressParameterResolver;
import edu.hm.praegla.parameterResolver.CustomerParameterResolver;
import edu.hm.praegla.parameterResolver.InventoryItemParameterResolver;
import edu.hm.praegla.shoppingcart.dto.ShoppingCartDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({AddressParameterResolver.class, CustomerParameterResolver.class, InventoryItemParameterResolver.class})
public class OrderTest extends BrickstoreRestTest {

    private final InventoryClient inventoryClient;
    private final AccountClient accountClient;
    private final OrderClient orderClient;
    private final ShoppingCartClient shoppingCartClient;

    public OrderTest() {
        inventoryClient = new InventoryClient(spec);
        orderClient = new OrderClient(spec);
        accountClient = new AccountClient(spec);
        shoppingCartClient = new ShoppingCartClient(spec);
    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ModifyInventoryItemOfOrderTest {

        private OrderDTO testOrder;
        private InventoryItemDTO testInventoryItem;

        @BeforeEach
        public void beforeEach(CustomerDTO customerDTO, AddressDTO addressDTO, InventoryItemDTO inventoryItemDTO) {
            AccountDTO testAccount = accountClient.createAccount(customerDTO, addressDTO);
            accountClient.chargeAccount(testAccount.getId(), new BigDecimal("200.00"));

            inventoryItemDTO.setPrice(new BigDecimal("19.99"));
            testInventoryItem = inventoryClient.createInventoryItem(inventoryItemDTO);
            shoppingCartClient.addShoppingCartItem(testAccount.getId(), testInventoryItem.getId(), 5);
            ShoppingCartDTO shoppingCart = shoppingCartClient.getShoppingCartByAccountId(testAccount.getId());
            testOrder = orderClient.createProcessedOrder(testAccount, shoppingCart);
        }

        @Test
        public void shouldNotChangeOrderTotalWhenInventoryItemPriceIsChanged() {

            testInventoryItem.setPrice(new BigDecimal("109.99"));
            inventoryClient.updateInventoryItem(testInventoryItem);

            OrderDTO order = orderClient.getOrder(testOrder.getId());
            assertThat(order.getTotal()).isEqualTo(new BigDecimal("99.95"));

            OrderItemDTO orderItemDTO = order.getOrderItems().get(0);
            assertThat(orderItemDTO.getInventoryItemId()).isEqualTo(testInventoryItem.getId());
            assertThat(orderItemDTO.getPrice()).isEqualTo(new BigDecimal("19.99"));
        }

    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
    class ChangeOrderStatusTest {

        private OrderDTO testOrder;

        @BeforeAll
        public void beforeAll(CustomerDTO customerDTO, AddressDTO addressDTO, InventoryItemDTO inventoryItemDTO) {
            AccountDTO testAccount = accountClient.createAccount(customerDTO, addressDTO);
            accountClient.chargeAccount(testAccount.getId(), new BigDecimal("200.00"));

            inventoryItemDTO.setPrice(new BigDecimal("19.99"));
            InventoryItemDTO testInventoryItem = inventoryClient.createInventoryItem(inventoryItemDTO);
            shoppingCartClient.addShoppingCartItem(testAccount.getId(), testInventoryItem.getId(), 5);
            ShoppingCartDTO shoppingCart = shoppingCartClient.getShoppingCartByAccountId(testAccount.getId());
            testOrder = orderClient.createProcessedOrder(testAccount, shoppingCart);
        }

        @ParameterizedTest
        @ValueSource(strings = {"SHIPPED", "DELIVERED"})
        public void shouldChangeOrderStatus(String status) {
            orderClient.updateOrderStatus(testOrder.getId(), status)
                    .then()
                    .statusCode(200);
            OrderDTO order = orderClient.getOrder(testOrder.getId());
            assertThat(order.getStatus()).isEqualTo(status);
        }
    }

}
