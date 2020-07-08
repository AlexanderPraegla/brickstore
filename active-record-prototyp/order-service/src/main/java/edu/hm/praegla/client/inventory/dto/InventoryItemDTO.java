package edu.hm.praegla.client.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryItemDTO {
    private long id;
    private String name;
    private BigDecimal price;
    private int stock;
    private int deliveryTime;
    private InventoryItemStatus status;
}
