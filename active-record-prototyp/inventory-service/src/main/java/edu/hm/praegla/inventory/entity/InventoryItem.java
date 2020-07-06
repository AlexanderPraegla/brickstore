package edu.hm.praegla.inventory.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;
    @Min(1)
    private double price;
    @Min(0)
    private int stock;
    @Min(1)
    private int deliveryTime;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private InventoryItemStatus status;

}
