package edu.hm.praegla.shoppingcart;

import edu.hm.praegla.ApiClient;
import edu.hm.praegla.shoppingcart.dto.ShoppingCartDTO;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ShoppingCartClient extends ApiClient {

    public ShoppingCartClient(RequestSpecification spec) {
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
        Map<String, Number> body = new HashMap<>();
        body.put("accountId", accountId);
        body.put("quantity", quantity);
        body.put("inventoryItemId", inventoryItemId);
        return given(spec)
                .when()
                .body(body)
                .put("shopping-carts/");

    }

    public Response deleteShoppingCartItem(long lineItemId, long accountId) {
        return given(spec)
                .when()
                .delete("shopping-carts/{accountId}/items/{lineItemId}", accountId, lineItemId);
    }

}
