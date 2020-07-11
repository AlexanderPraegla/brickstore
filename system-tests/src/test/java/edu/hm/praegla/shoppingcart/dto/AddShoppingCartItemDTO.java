package edu.hm.praegla.shoppingcart.dto;

import lombok.Data;

@Data
public class AddShoppingCartItemDTO {
    private long accountId;
    private long inventoryItemId;
    private int quantity;

}
