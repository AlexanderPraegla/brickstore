package edu.hm.praegla.order;

import edu.hm.praegla.ApiClient;
import edu.hm.praegla.order.dto.OrderDTO;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderClient extends ApiClient {

    protected OrderClient(RequestSpecification spec) {
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

    public Response createOrder(long accountId) {
        Map<String, Number> body = new HashMap<>();
        body.put("accountId", accountId);
        return given(spec)
                .when()
                .body(body)
                .put("orders/");

    }

    public Response updateOrderStatus(long orderId, String status) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);
        return given(spec)
                .when()
                .body(body)
                .post("orders/{orderId}/status", orderId);
    }
}
