package edu.hm.brickstore.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.hm.brickstore.error.ResponseCode;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    private long id;
    private long accountId;
    private BigDecimal total;
    private ShippingAddress shippingAddress;
    private List<OrderItem> orderItems;
    private ResponseCode errorCode;
}
