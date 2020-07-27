package edu.hm.brickstore.order;

import edu.hm.brickstore.BrickstoreRestTest;
import edu.hm.brickstore.account.dto.AccountDTO;
import edu.hm.brickstore.account.dto.AddressDTO;
import edu.hm.brickstore.account.dto.CustomerDTO;
import edu.hm.brickstore.client.AccountTestTestClient;
import edu.hm.brickstore.client.InventoryTestTestClient;
import edu.hm.brickstore.client.OrderTestTestClient;
import edu.hm.brickstore.client.ShoppingCartTestTestClient;
import edu.hm.brickstore.inventory.dto.InventoryItemDTO;
import edu.hm.brickstore.order.dto.OrderDTO;
import edu.hm.brickstore.order.dto.OrderItemDTO;
import edu.hm.brickstore.parameterResolver.AddressParameterResolver;
import edu.hm.brickstore.parameterResolver.CustomerParameterResolver;
import edu.hm.brickstore.parameterResolver.InventoryItemParameterResolver;
import edu.hm.brickstore.shoppingcart.dto.ShoppingCartDTO;
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

    private final InventoryTestTestClient inventoryTestClient;
    private final AccountTestTestClient accountTestClient;
    private final OrderTestTestClient orderTestClient;
    private final ShoppingCartTestTestClient shoppingCartTestClient;

    public OrderTest() {
        inventoryTestClient = new InventoryTestTestClient(spec);
        orderTestClient = new OrderTestTestClient(spec);
        accountTestClient = new AccountTestTestClient(spec);
        shoppingCartTestClient = new ShoppingCartTestTestClient(spec);
    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ModifyInventoryItemOfOrderTest {

        private OrderDTO testOrder;
        private InventoryItemDTO testInventoryItem;

        @BeforeEach
        public void beforeEach(CustomerDTO customerDTO, AddressDTO addressDTO, InventoryItemDTO inventoryItemDTO) {
            AccountDTO testAccount = accountTestClient.createAccount(customerDTO, addressDTO);
            accountTestClient.creditAccount(testAccount.getId(), new BigDecimal("200.00"));

            inventoryItemDTO.setPrice(new BigDecimal("19.99"));
            testInventoryItem = inventoryTestClient.createInventoryItem(inventoryItemDTO);
            shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), testInventoryItem.getId(), 5);
            ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
            testOrder = orderTestClient.createProcessedOrder(testAccount, shoppingCart);
        }

        @Test
        public void shouldNotChangeOrderTotalWhenInventoryItemPriceIsChanged() {

            testInventoryItem.setPrice(new BigDecimal("109.99"));
            inventoryTestClient.updateInventoryItem(testInventoryItem);

            OrderDTO order = orderTestClient.getOrder(testOrder.getId());
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
            AccountDTO testAccount = accountTestClient.createAccount(customerDTO, addressDTO);
            accountTestClient.creditAccount(testAccount.getId(), new BigDecimal("200.00"));

            inventoryItemDTO.setPrice(new BigDecimal("19.99"));
            InventoryItemDTO testInventoryItem = inventoryTestClient.createInventoryItem(inventoryItemDTO);
            shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), testInventoryItem.getId(), 5);
            ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
            testOrder = orderTestClient.createProcessedOrder(testAccount, shoppingCart);
        }

        @ParameterizedTest
        @ValueSource(strings = {"SHIPPED", "DELIVERED"})
        public void shouldChangeOrderStatus(String status) {
            orderTestClient.updateOrderStatus(testOrder.getId(), status)
                    .then()
                    .statusCode(200);
            OrderDTO order = orderTestClient.getOrder(testOrder.getId());
            assertThat(order.getStatus()).isEqualTo(status);
        }
    }

}
