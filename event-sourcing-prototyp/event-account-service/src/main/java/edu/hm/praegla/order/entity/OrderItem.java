package edu.hm.praegla.order.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderItem {

    private long id;
    @Min(1)
    private long inventoryItemId;
    @Min(1)
    private long deliveryTime;
    @NotNull
    private String name;
    @Digits(integer = 7, fraction = 2)
    private BigDecimal price;
    @Min(1)
    private int quantity;

}
