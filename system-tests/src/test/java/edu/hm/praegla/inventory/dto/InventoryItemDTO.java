package edu.hm.praegla.inventory.dto;

import lombok.Data;

@Data
public class InventoryItemDTO {
    private long id;
    private String name;
    private double price;
    private int stock;
    private int deliveryTime;
    private String status;
}
