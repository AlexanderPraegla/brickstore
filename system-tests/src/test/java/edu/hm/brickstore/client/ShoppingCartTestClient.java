package edu.hm.brickstore.client;

import edu.hm.brickstore.shoppingcart.dto.AddShoppingCartItemDTO;
import edu.hm.brickstore.shoppingcart.dto.ShoppingCartDTO;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ShoppingCartTestClient extends ApiTestClient {

    public ShoppingCartTestClient(RequestSpecification spec) {
        super(spec);
    }

    public ShoppingCartDTO getShoppingCartByAccountId(long accountId) {
        return getResourceById("shopping-carts/{accountId}", accountId, ShoppingCartDTO.class);
    }

    public List<ShoppingCartDTO> getShoppingCarts() {
        return given(spec)
                .when()
                .get("shopping-carts/")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList(".", ShoppingCartDTO.class);
    }

    public Response addShoppingCartItem(long accountId, long inventoryItemId, int quantity) {
        AddShoppingCartItemDTO addShoppingCartItemDTO = new AddShoppingCartItemDTO();
        addShoppingCartItemDTO.setAccountId(accountId);
        addShoppingCartItemDTO.setInventoryItemId(inventoryItemId);
        addShoppingCartItemDTO.setQuantity(quantity);

        return given(spec)
                .when()
                .body(addShoppingCartItemDTO)
                .put("shopping-carts/");

    }

    public Response deleteShoppingCartItem(long lineItemId, long accountId) {
        return given(spec)
                .when()
                .delete("shopping-carts/{accountId}/items/{lineItemId}", accountId, lineItemId);
    }

}
