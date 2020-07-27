package edu.hm.brickstore.inventory.dto;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class ChangeInventoryItemStockDTO {
    private long inventoryItemId;
    @Min(1)
    private int quantity;
}
