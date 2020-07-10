package edu.hm.praegla.inventory.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class InventoryItemDTO {
    private long id;
    private String name;
    private BigDecimal price;
    private int stock;
    private int deliveryTime;
    private String status;

    public InventoryItemDTO(int deliveryTime, String name, BigDecimal price, String status, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.deliveryTime = deliveryTime;
        this.status = status;
    }
}
