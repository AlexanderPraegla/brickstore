package edu.hm.brickstore.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInventoryItemsStockDTO {

    private List<@Valid Item> items;

    @Data
    public static class Item {
        private long inventoryItemId;
        @Min(1)
        private int quantity;
    }
}
