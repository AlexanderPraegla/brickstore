package edu.hm.brickstore.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDTO {

    private long id;
    private long inventoryItemId;
    private String name;
    private BigDecimal price;
    private int quantity;
    private int deliveryTime;
}
