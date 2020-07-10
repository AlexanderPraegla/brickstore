package edu.hm.praegla.client;

import edu.hm.praegla.ApiClient;
import edu.hm.praegla.order.dto.OrderDTO;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderClient extends ApiClient {

    public OrderClient(RequestSpecification spec) {
        super(spec);
    }

    public OrderDTO getOrder(long orderId) {
        return getResourceById("orders/{orderId}", orderId, OrderDTO.class);
    }

    public List<OrderDTO> getOpenOrders() {
        return given(spec)
                .when()
                .get("orders/open")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList(".", OrderDTO.class);
    }

    public OrderDTO getLatestOrderForAccount(long accountId) {
        return given(spec)
                .when()
                .get("orders/account/{accountId}", accountId)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList(".", OrderDTO.class)
                .get(0);
    }

    public Response createOrderRequest(long accountId) {
        Map<String, Number> body = new HashMap<>();
        body.put("accountId", accountId);
        return given(spec)
                .when()
                .body(body)
                .put("orders/");

    }

    public OrderDTO createOrder(long accountId) {
        String createdOrderLocation = createOrderRequest(accountId)
                .then()
                .statusCode(201)
                .extract().header("location");
        return getResourceByLocationHeader(createdOrderLocation, OrderDTO.class);
    }

    public Response updateOrderStatus(long orderId, String status) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);
        return given(spec)
                .when()
                .body(body)
                .post("orders/{orderId}/status", orderId);
    }

    public Response cancelOrder(long orderId) {
        return given(spec)
                .when()
                .post("orders/{orderId}/cancellation", orderId);
    }
}
