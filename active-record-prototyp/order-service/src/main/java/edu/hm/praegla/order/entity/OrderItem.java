package edu.hm.praegla.order.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Min(1)
    @Column(name = "inventory_item_id")
    private long inventoryItemId;
    @Min(1)
    private long deliveryTime;
    @NotNull
    private String name;
    @DecimalMin(value = "1")
    @Column(precision = 7, scale = 2)
    private BigDecimal price;
    @Min(1)
    private int quantity;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonBackReference
    private Order order;

    public OrderItem(long inventoryItemId, int quantity) {
        this.inventoryItemId = inventoryItemId;
        this.quantity = quantity;
    }
}
