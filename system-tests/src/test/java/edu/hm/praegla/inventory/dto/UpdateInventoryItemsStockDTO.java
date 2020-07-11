package edu.hm.praegla.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class UpdateInventoryItemsStockDTO {

    private List<Item> items;

    @Data
    @AllArgsConstructor
    public static class Item {
        private long inventoryItemId;
        private int quantity;
    }
}
