package edu.hm.praegla.client;

import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.order.dto.OrderDTO;
import edu.hm.praegla.order.dto.OrderItemDTO;
import edu.hm.praegla.order.dto.OrderStatusUpdateDTO;
import edu.hm.praegla.order.dto.ShippingAddressDTO;
import edu.hm.praegla.shoppingcart.dto.ShoppingCartDTO;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class OrderTestTestClient extends ApiTestClient {

    public OrderTestTestClient(RequestSpecification spec) {
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

    public Response createOrderRequest(AccountDTO accountDTO, ShoppingCartDTO shoppingCart) {
        OrderDTO orderDTO = createOrderDTO(accountDTO, shoppingCart);
        return given(spec)
                .when()
                .body(orderDTO)
                .put("orders/");

    }

    public OrderDTO createOrder(AccountDTO accountDTO, ShoppingCartDTO shoppingCart) {
        OrderDTO orderDTO = createOrderDTO(accountDTO, shoppingCart);

        String createdOrderLocation = given(spec)
                .when()
                .body(orderDTO)
                .put("orders/")
                .then()
                .statusCode(201)
                .extract().header("location");
        return getResourceByLocationHeader(createdOrderLocation, OrderDTO.class);
    }

    public OrderDTO createOrder(long accountId) {
        String createdOrderLocation = createOrderRequest(accountId)
                .then()
                .statusCode(201)
                .extract().header("location");
        return getResourceByLocationHeader(createdOrderLocation, OrderDTO.class);
    }

    public Response updateOrderStatus(long orderId, String status) {
        OrderStatusUpdateDTO updateOrderStatusDTO = new OrderStatusUpdateDTO(orderId, status);
        return given(spec)
                .when()
                .body(updateOrderStatusDTO)
                .post("orders/status");
    }

    public Response cancelOrder(long orderId) {
        return given(spec)
                .when()
                .post("orders/{orderId}/cancellation", orderId);
    }

    private OrderDTO createOrderDTO(AccountDTO accountDTO, ShoppingCartDTO shoppingCart) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setAccountId(accountDTO.getId());

        ShippingAddressDTO shippingAddressDTO = new ShippingAddressDTO();
        String firstname = accountDTO.getCustomer().getFirstname();
        String lastname = accountDTO.getCustomer().getLastname();
        shippingAddressDTO.setCustomerName(firstname + " " + lastname);
        shippingAddressDTO.setCity(accountDTO.getAddress().getCity());
        shippingAddressDTO.setStreet(accountDTO.getAddress().getStreet());
        shippingAddressDTO.setPostalCode(accountDTO.getAddress().getPostalCode());
        orderDTO.setShippingAddress(shippingAddressDTO);

        List<OrderItemDTO> orderItems = shoppingCart.getLineItems()
                .stream()
                .map(lineItemDTO -> {
                    OrderItemDTO orderItemDTO = new OrderItemDTO();
                    orderItemDTO.setInventoryItemId(lineItemDTO.getInventoryItemId());
                    orderItemDTO.setName(lineItemDTO.getName());
                    orderItemDTO.setPrice(lineItemDTO.getPrice());
                    orderItemDTO.setQuantity(lineItemDTO.getQuantity());
                    orderItemDTO.setDeliveryTime(lineItemDTO.getDeliveryTime());
                    return orderItemDTO;
                })
                .collect(Collectors.toList());
        orderDTO.setOrderItems(orderItems);

        BigDecimal total = orderItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orderDTO.setTotal(total);
        return orderDTO;
    }

    public OrderDTO createProcessedOrder(AccountDTO accountDTO, ShoppingCartDTO shoppingCart) {
        OrderDTO testOrder = createOrder(accountDTO, shoppingCart);

        long orderId = testOrder.getId();
        AwaitilityHelper.wait(() -> {
            OrderDTO o = getOrder(orderId);
            return StringUtils.equals(o.getStatus(), "PROCESSED");
        });

        testOrder = getOrder(testOrder.getId());
        return testOrder;
    }

    public OrderDTO createOrderWithStatus(AccountDTO accountDTO, ShoppingCartDTO shoppingCart, String status) {
        OrderDTO testOrder = createOrder(accountDTO, shoppingCart);

        long orderId = testOrder.getId();
        AwaitilityHelper.wait(() -> {
            OrderDTO o = getOrder(orderId);
            return StringUtils.equals(o.getStatus(), status);
        });

        testOrder = getOrder(testOrder.getId());
        return testOrder;
    }
}
