package edu.hm.brickstore.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrderDTO {

    private long id;
    private List<OrderItemDTO> orderItems;
    private BigDecimal total;
    private String status;
    private long accountId;
    private ShippingAddressDTO shippingAddress;
    private String errorCode;
}
