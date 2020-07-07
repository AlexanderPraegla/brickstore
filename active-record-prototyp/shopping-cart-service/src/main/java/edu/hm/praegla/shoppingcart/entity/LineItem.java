package edu.hm.praegla.shoppingcart.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;

@Data
@Entity
@NoArgsConstructor
public class LineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Min(1)
    private long inventoryItemId;
    @Min(1)
    private int quantity;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonBackReference
    private ShoppingCart shoppingCart;

    public LineItem(long inventoryItemId, int quantity) {
        this.inventoryItemId = inventoryItemId;
        this.quantity = quantity;
    }
}
