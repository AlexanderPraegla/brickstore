package edu.hm.praegla.shoppingcart;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.error.dto.ApiErrorDTO;
import edu.hm.praegla.shoppingcart.dto.ShoppingCartDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShoppingCartTest extends BrickstoreRestTest {

    private final ShoppingCartClient shoppingCartClient;

    public ShoppingCartTest() {
        shoppingCartClient = new ShoppingCartClient(spec);
    }

    @Test
    @Order(1)
    public void shouldGetShoppingCarts() {
        List<ShoppingCartDTO> accounts = shoppingCartClient.getShoppingCarts();
        assertThat(accounts).hasSize(2);
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ShouldAddItemsToShoppingCartAndCheckSize {

        private final static long ACCOUNT_ID = 12;

        @ParameterizedTest
        @CsvSource({"11,1", "12,1", "13,3"})
        @Order(2)
        public void shouldAddItemsToShoppingCart(long inventoryItemId, int quantity) {
            shoppingCartClient.addShoppingCartItem(ACCOUNT_ID, inventoryItemId, quantity)
                    .then()
                    .statusCode(200);
        }

        @Test
        @Order(3)
        public void shouldHaveThreeItemsInShoppingCart() {
            ShoppingCartDTO shoppingCart = shoppingCartClient.getShoppingCartByAccountId(ACCOUNT_ID);
            assertThat(shoppingCart.getLineItems()).hasSize(3);
        }

    }

    @Test
    @Order(4)
    public void shouldRemoveItemFromShoppingCart() {
        long lineItemId = 2;
        long accountId = 14;
        shoppingCartClient.deleteShoppingCartItem(lineItemId, accountId)
                .then()
                .statusCode(200);
        ShoppingCartDTO shoppingCart = shoppingCartClient.getShoppingCartByAccountId(accountId);
        assertThat(shoppingCart.getLineItems()).hasSize(1);
    }


    @Test
    public void shouldFailAddingItemToCartWithInactiveAccount() {
        int quantity = 2;
        int inventoryItemId = 11;
        long accountId = 13;
        ApiErrorDTO apiErrorDTO = shoppingCartClient.addShoppingCartItem(accountId, inventoryItemId, quantity)
                .then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ACCOUNT_INACTIVE");
    }

    @Test
    public void shouldFailAddingDeactivatedItemToCart() {
        int quantity = 1;
        int inventoryItemId = 5;
        long accountId = 10;

        ApiErrorDTO apiErrorDTO = shoppingCartClient.addShoppingCartItem(accountId, inventoryItemId, quantity)
                .then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ITEM_NOT_ORDERABLE");
    }


}
