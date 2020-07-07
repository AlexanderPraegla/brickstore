package edu.hm.praegla.shoppingcart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineItemDTO {

    private long lineItemId;
    private long inventoryItemId;
    private String name;
    private double price;
    private int quantity;
}
