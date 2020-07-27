package edu.hm.brickstore.shoppingcart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveShoppingCartItemDTO {

    private long accountId;
    private long lineItemId;
}
