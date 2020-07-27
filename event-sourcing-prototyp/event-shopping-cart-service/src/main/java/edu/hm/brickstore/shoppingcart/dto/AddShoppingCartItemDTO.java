package edu.hm.brickstore.shoppingcart.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
public class AddShoppingCartItemDTO {
    @Min(1)
    private long accountId;
    @Min(1)
    private long inventoryItemId;
    @Min(1)
    private int quantity;
}
