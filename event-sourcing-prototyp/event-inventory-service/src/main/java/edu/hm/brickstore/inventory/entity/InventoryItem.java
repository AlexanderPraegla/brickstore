package edu.hm.brickstore.inventory.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Document(collection = "inventory_item")
public class InventoryItem {

    @Transient
    public static final String SEQUENCE_NAME = "inventory_item_sequence";

    @Id
    private long id;

    @NotNull
    private String name;
    @DecimalMin(value = "1")
    @Digits(integer = 7, fraction = 2)
    private BigDecimal price;
    @Min(0)
    private int stock;
    @Min(1)
    private int deliveryTime;

    @NotNull
    private InventoryItemStatus status;

}
