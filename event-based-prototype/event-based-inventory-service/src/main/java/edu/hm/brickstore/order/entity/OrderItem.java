package edu.hm.brickstore.order.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderItem {

    @Transient
    public static final String SEQUENCE_NAME = "order-item-sequence";

    @Id
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
