package edu.hm.brickstore.shoppingcart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineItemDTO {

    private long lineItemId;
    private long inventoryItemId;
    private String name;
    private BigDecimal price;
    private int quantity;
    private int deliveryTime;
}
