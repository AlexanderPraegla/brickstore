package edu.hm.praegla.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDTO {

    private long id;
    private long inventoryItemId;
    private String name;
    private double price;
    private int quantity;
    private int deliveryTime;
}
