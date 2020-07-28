package edu.hm.brickstore.inventory.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Entity
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;
    @DecimalMin(value = "1")
    @Column(precision = 7, scale = 2)
    private BigDecimal price;
    @Min(0)
    private int stock;
    @Min(1)
    private int deliveryTime;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private InventoryItemStatus status;

}
