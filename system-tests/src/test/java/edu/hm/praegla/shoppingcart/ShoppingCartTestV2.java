package edu.hm.praegla.shoppingcart;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.account.dto.AddressDTO;
import edu.hm.praegla.account.dto.CustomerDTO;
import edu.hm.praegla.client.AccountClient;
import edu.hm.praegla.client.InventoryClient;
import edu.hm.praegla.client.ShoppingCartClient;
import edu.hm.praegla.error.dto.ApiErrorDTO;
import edu.hm.praegla.inventory.dto.InventoryItemDTO;
import edu.hm.praegla.parameterResolver.AddressParameterResolver;
import edu.hm.praegla.parameterResolver.CustomerParameterResolver;
import edu.hm.praegla.parameterResolver.InventoryItemParameterResolver;
import edu.hm.praegla.shoppingcart.dto.LineItemDTO;
import edu.hm.praegla.shoppingcart.dto.ShoppingCartDTO;
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

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({AddressParameterResolver.class, CustomerParameterResolver.class, InventoryItemParameterResolver.class})
public class ShoppingCartTestV2 extends BrickstoreRestTest {

    private final ShoppingCartClient shoppingCartClient;
    private final AccountClient accountClient;
    private final InventoryClient inventoryClient;

    public ShoppingCartTestV2() {
        shoppingCartClient = new ShoppingCartClient(spec);
        accountClient = new AccountClient(spec);
        inventoryClient = new InventoryClient(spec);
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AddAndRemoveItems {

        private AccountDTO testAccount;

        @BeforeAll
        void beforeAll(CustomerDTO customerDTO, AddressDTO addressDTO) {
            testAccount = accountClient.createAccount(customerDTO, addressDTO);
        }

        @RepeatedTest(value = 3)
        @Order(1)
        public void shouldAddItemsToShoppingCart(InventoryItemDTO inventoryItemDTO) {
            InventoryItemDTO inventoryItem = inventoryClient.createInventoryItem(inventoryItemDTO);
            shoppingCartClient.addShoppingCartItem(testAccount.getId(), inventoryItem.getId(), 1)
                    .then()
                    .statusCode(200);
        }

        @Test
        @Order(2)
        public void shouldHaveThreeItemsInShoppingCart() {
            ShoppingCartDTO shoppingCart = shoppingCartClient.getShoppingCartByAccountId(testAccount.getId());
            assertThat(shoppingCart.getLineItems()).hasSize(3);
        }

        @Test
        @Order(3)
        public void shouldRemoveItemFromShoppingCart() {
            ShoppingCartDTO shoppingCart = shoppingCartClient.getShoppingCartByAccountId(testAccount.getId());
            LineItemDTO lineItem = shoppingCart.getLineItems().get(0);

            shoppingCartClient.deleteShoppingCartItem(lineItem.getLineItemId(), testAccount.getId())
                    .then()
                    .statusCode(200);

            shoppingCart = shoppingCartClient.getShoppingCartByAccountId(testAccount.getId());

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
            testAccount = accountClient.createAccount(customerDTO, addressDTO);
            testInventoryItem = inventoryClient.createInventoryItem(inventoryItemDTO);
        }

        @Test
        public void shouldFailAddingItemToCartWithDeactivatedAccount() {
            int quantity = 2;

            accountClient.updateAccountStatus(testAccount.getId(), "DEACTIVATED")
                    .then()
                    .statusCode(200);
            ApiErrorDTO apiErrorDTO = shoppingCartClient.addShoppingCartItem(testAccount.getId(), testInventoryItem.getId(), quantity)
                    .then()
                    .statusCode(400)
                    .extract()
                    .as(ApiErrorDTO.class);

            assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ACCOUNT_DEACTIVATED");
        }

        @Test
        public void shouldFailAddingDeactivatedItemToCart() {
            int quantity = 1;
            inventoryClient.updateInventoryItemStatus(testInventoryItem.getId(), "DEACTIVATED")
                    .then()
                    .statusCode(200);

            ApiErrorDTO apiErrorDTO = shoppingCartClient.addShoppingCartItem(testAccount.getId(), testInventoryItem.getId(), quantity)
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
            inventoryClient.updateInventoryItem(testInventoryItem);

            int quantity = 1;

            ApiErrorDTO apiErrorDTO = shoppingCartClient.addShoppingCartItem(testAccount.getId(), testInventoryItem.getId(), quantity)
                    .then()
                    .statusCode(400)
                    .extract()
                    .as(ApiErrorDTO.class);

            assertThat(apiErrorDTO.getResponseCode()).isEqualTo("OUT_OF_STOCK");
        }
    }


}
