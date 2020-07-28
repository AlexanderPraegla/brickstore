package edu.hm.brickstore.inventory.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Data
public class UpdateInventoryItemsStockDTO {

    private List<@Valid Item> items;

    @Data
    public static class Item {
        private long inventoryItemId;
        @Min(1)
        private int quantity;
    }
}
