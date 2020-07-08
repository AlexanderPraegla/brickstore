package edu.hm.praegla.client.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
public class ChangeInventoryItemStockDTO {
    public long inventoryItemId;
    @Min(1)
    public int quantity;
}
