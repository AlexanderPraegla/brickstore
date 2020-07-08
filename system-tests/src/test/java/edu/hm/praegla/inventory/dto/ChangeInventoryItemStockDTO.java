package edu.hm.praegla.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeInventoryItemStockDTO {
    private long inventoryItemId;
    private int quantity;
}
