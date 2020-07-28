package edu.hm.brickstore.shoppingcart;

import edu.hm.brickstore.BrickstoreRestTest;
import edu.hm.brickstore.account.dto.AccountDTO;
import edu.hm.brickstore.account.dto.AddressDTO;
import edu.hm.brickstore.account.dto.CustomerDTO;
import edu.hm.brickstore.client.AccountTestClient;
import edu.hm.brickstore.client.InventoryTestClient;
import edu.hm.brickstore.client.ShoppingCartTestClient;
import edu.hm.brickstore.error.dto.ApiErrorDTO;
import edu.hm.brickstore.inventory.dto.InventoryItemDTO;
import edu.hm.brickstore.parameterResolver.AddressParameterResolver;
import edu.hm.brickstore.parameterResolver.CustomerParameterResolver;
import edu.hm.brickstore.parameterResolver.InventoryItemParameterResolver;
import edu.hm.brickstore.shoppingcart.dto.LineItemDTO;
import edu.hm.brickstore.shoppingcart.dto.ShoppingCartDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({AddressParameterResolver.class, CustomerParameterResolver.class, InventoryItemParameterResolver.class})
public class ShoppingCartTest extends BrickstoreRestTest {

    private final ShoppingCartTestClient shoppingCartTestClient;
    private final AccountTestClient accountTestClient;
    private final InventoryTestClient inventoryTestClient;

    public ShoppingCartTest() {
        shoppingCartTestClient = new ShoppingCartTestClient(spec);
        accountTestClient = new AccountTestClient(spec);
        inventoryTestClient = new InventoryTestClient(spec);
    }

    @Test
    public void shouldGetAllAvailableShoppingCarts(CustomerDTO customerDTO, AddressDTO addressDTO, InventoryItemDTO inventoryItemDTO) {
        AccountDTO testAccount = accountTestClient.createAccount(customerDTO, addressDTO);
        InventoryItemDTO inventoryItem = inventoryTestClient.createInventoryItem(inventoryItemDTO);
        shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), inventoryItem.getId(), 1)
                .then()
                .statusCode(200);

        List<ShoppingCartDTO> shoppingCarts = shoppingCartTestClient.getShoppingCarts();
        assertThat(shoppingCarts).isNotEmpty();
        assertThat(shoppingCarts.size()).isGreaterThanOrEqualTo(1);
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AddAndRemoveItems {

        private AccountDTO testAccount;

        @BeforeAll
        void beforeAll(CustomerDTO customerDTO, AddressDTO addressDTO) {
            testAccount = accountTestClient.createAccount(customerDTO, addressDTO);
        }

        @RepeatedTest(value = 3)
        @Order(1)
        public void shouldAddItemsToShoppingCart(InventoryItemDTO inventoryItemDTO) {
            InventoryItemDTO inventoryItem = inventoryTestClient.createInventoryItem(inventoryItemDTO);
            shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), inventoryItem.getId(), 1)
                    .then()
                    .statusCode(200);
        }

        @Test
        @Order(2)
        public void shouldHaveThreeItemsInShoppingCart() {
            ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
            assertThat(shoppingCart.getLineItems()).hasSize(3);
        }

        @Test
        @Order(3)
        public void shouldRemoveItemFromShoppingCart() {
            ShoppingCartDTO shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());
            LineItemDTO lineItem = shoppingCart.getLineItems().get(0);

            shoppingCartTestClient.deleteShoppingCartItem(lineItem.getLineItemId(), testAccount.getId())
                    .then()
                    .statusCode(200);

            shoppingCart = shoppingCartTestClient.getShoppingCartByAccountId(testAccount.getId());

            int expectedCartSize = 2;
            assertThat(shoppingCart.getLineItems()).hasSize(expectedCartSize);
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FailAddingItems {

        private AccountDTO testAccount;
        private InventoryItemDTO testInventoryItem;

        @BeforeEach
        void beforeEach(CustomerDTO customerDTO, AddressDTO addressDTO, InventoryItemDTO inventoryItemDTO) {
            testAccount = accountTestClient.createAccount(customerDTO, addressDTO);
            testInventoryItem = inventoryTestClient.createInventoryItem(inventoryItemDTO);
        }

        @Test
        public void shouldFailAddingItemToCartWithDeactivatedAccount() {
            int quantity = 2;

            accountTestClient.updateAccountStatus(testAccount.getId(), "DEACTIVATED")
                    .then()
                    .statusCode(200);
            ApiErrorDTO apiErrorDTO = shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), testInventoryItem.getId(), quantity)
                    .then()
                    .statusCode(400)
                    .extract()
                    .as(ApiErrorDTO.class);

            assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ACCOUNT_DEACTIVATED");
        }

        @Test
        public void shouldFailAddingDeactivatedItemToCart() {
            int quantity = 1;
            inventoryTestClient.updateInventoryItemStatus(testInventoryItem.getId(), "DEACTIVATED")
                    .then()
                    .statusCode(200);

            ApiErrorDTO apiErrorDTO = shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), testInventoryItem.getId(), quantity)
                    .then()
                    .statusCode(400)
                    .extract()
                    .as(ApiErrorDTO.class);

            assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ITEM_NOT_ORDERABLE");
        }

        @Test
        public void shouldFailAddingOutOfStockItemToCart() {
            testInventoryItem.setStock(0);
            testInventoryItem.setStatus("OUT_OF_STOCK");
            inventoryTestClient.updateInventoryItem(testInventoryItem);

            int quantity = 1;

            ApiErrorDTO apiErrorDTO = shoppingCartTestClient.addShoppingCartItem(testAccount.getId(), testInventoryItem.getId(), quantity)
                    .then()
                    .statusCode(400)
                    .extract()
                    .as(ApiErrorDTO.class);

            assertThat(apiErrorDTO.getResponseCode()).isEqualTo("OUT_OF_STOCK");
        }
    }


}
