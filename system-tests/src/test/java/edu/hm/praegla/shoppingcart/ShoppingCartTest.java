package edu.hm.praegla.shoppingcart;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.error.dto.ApiErrorDTO;
import edu.hm.praegla.inventory.dto.InventoryItemDTO;
import edu.hm.praegla.shoppingcart.dto.ShoppingCartDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShoppingCartTest extends BrickstoreRestTest {

    @Test
    @Order(1)
    public void shouldGetShoppingCarts() {
        List<ShoppingCartDTO> accounts = given(spec)
                .when()
                .get("shopping-carts/")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList(".", ShoppingCartDTO.class);
        assertThat(accounts).hasSize(2);
    }

    @Test
    public void shouldFailAddingItemToCartWithInactiveAccount() {
        int quantity = 2;
        int inventoryItemId = 11;
        long accountId = 13;
        ApiErrorDTO apiErrorDTO = addShoppingCartItem(accountId, inventoryItemId, quantity)
                .then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ACCOUNT_INACTIVE");
    }

    @Test
    public void shouldFailAddingItemToCartWithoutEnoughStock() {
        int quantity = 2;
        int inventoryItemId = 14;
        long accountId = 10;

        ApiErrorDTO apiErrorDTO = addShoppingCartItem(accountId, inventoryItemId, quantity)
                .then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("NOT_ENOUGH_STOCK");
    }

    @Test
    public void shouldFailAddingItemToCartWithItemOutOfStock() {
        int quantity = 1;
        int inventoryItemId = 15;
        long accountId = 10;

        ApiErrorDTO apiErrorDTO = addShoppingCartItem(accountId, inventoryItemId, quantity)
                .then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("OUT_OF_STOCK");
    }

    @Test
    public void shouldFailAddingDeactivatedItemToCart() {
        int quantity = 1;
        int inventoryItemId = 5;
        long accountId = 10;

        ApiErrorDTO apiErrorDTO = addShoppingCartItem(accountId, inventoryItemId, quantity)
                .then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ITEM_NOT_ORDERABLE");
    }

    private Response addShoppingCartItem(long accountId, long inventoryItemId, int quantity) {
        Map<String, Number> body = new HashMap<>();
        body.put("accountId", accountId);
        body.put("quantity", quantity);
        body.put("inventoryItemId", inventoryItemId);
        return given(spec)
                .when()
                .body(body)
                .put("shopping-carts/");

    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ShouldAddItemsToShoppingCartAndCheckSize {

        private final static long ACCOUNT_ID = 12;

        @ParameterizedTest
        @CsvSource({"11,1", "12,1", "13,3"})
        @Order(2)
        public void shouldAddItemsToShoppingCart(long inventoryItemId, int quantity) {
            addShoppingCartItem(ACCOUNT_ID, inventoryItemId, quantity)
                    .then()
                    .statusCode(200);
        }

        @Test
        @Order(3)
        public void shouldHaveThreeItemsInShoppingCart() {
            ShoppingCartDTO shoppingCart = getShoppingCartByAccountId(ACCOUNT_ID);
            assertThat(shoppingCart.getLineItems()).hasSize(3);
        }


        @ParameterizedTest
        @CsvSource({"11,2", "12,0", "13,7"})
        @Order(4)
        public void shouldHaveDecreasedStockValueOfInventoryItemAfterAddingToCard(long inventoryItemId, int stock) {
            InventoryItemDTO item = getInventoryItemById(inventoryItemId);
            assertThat(item.getStock()).isEqualTo(stock);
        }

    }

    private InventoryItemDTO getInventoryItemById(long inventoryItemId) {
        return getResourceById("inventory/{inventoryItemId}", inventoryItemId, InventoryItemDTO.class);
    }

    private ShoppingCartDTO getShoppingCartByAccountId(long accountId) {
        return getResourceById("shopping-carts/{accountId}", accountId, ShoppingCartDTO.class);
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ShouldRemoveItemsFromShoppingCartAndCheckSize {

        private final static long ACCOUNT_ID = 14;

        @Test
        @Order(5)
        public void shouldRemoveItemFromShoppingCart() {
            long lineItemId = 2;
            given(spec)
                    .when()
                    .delete("shopping-carts/{accountId}/items/{lineItemId}", ACCOUNT_ID, lineItemId)
                    .then()
                    .statusCode(200);
            ShoppingCartDTO shoppingCart = getShoppingCartByAccountId(ACCOUNT_ID);
            assertThat(shoppingCart.getLineItems()).hasSize(1);
        }

        @Test
        @Order(6)
        public void shouldHaveIncreasedInventoryItemStockAfterRemovingFromCard() {
            long inventoryItemId = 18;
            int stock = 3;
            InventoryItemDTO item = getInventoryItemById(inventoryItemId);
            assertThat(item.getStock()).isEqualTo(stock);
        }

    }
}
