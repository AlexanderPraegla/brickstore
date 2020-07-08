package edu.hm.praegla.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {

    private long id;
    private List<OrderItemDTO> orderItems;
    private double total;
    private String status;
    private long accountId;
    private long createdOn;
    private ShippingAddressDTO shippingAddress;
}
