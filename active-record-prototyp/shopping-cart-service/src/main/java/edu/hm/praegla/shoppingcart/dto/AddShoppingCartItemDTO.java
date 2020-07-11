package edu.hm.praegla.shoppingcart.dto;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class AddShoppingCartItemDTO {
    @Min(1)
    private long accountId;
    @Min(1)
    private long inventoryItemId;
    @Min(1)
    private int quantity;
}
