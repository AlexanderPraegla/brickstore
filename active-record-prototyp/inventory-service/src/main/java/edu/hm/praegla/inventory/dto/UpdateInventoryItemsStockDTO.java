package edu.hm.praegla.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Data
public class UpdateInventoryItemsStockDTO {

    private List<@Valid Item> items;

    @Data
    @AllArgsConstructor
    public static class Item {
        @Min(1)
        private long inventoryItemId;
        @Min(1)
        private int quantity;
    }
}
