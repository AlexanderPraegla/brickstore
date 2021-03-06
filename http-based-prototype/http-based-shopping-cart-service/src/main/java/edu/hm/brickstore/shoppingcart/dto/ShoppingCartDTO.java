package edu.hm.brickstore.shoppingcart.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShoppingCartDTO {

    private long accountId;
    private String customerName;
    private List<LineItemDTO> lineItems;
}
