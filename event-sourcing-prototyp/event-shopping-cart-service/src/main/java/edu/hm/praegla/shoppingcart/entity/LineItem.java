package edu.hm.praegla.shoppingcart.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineItem {

    @Transient
    public static final String SEQUENCE_NAME = "line-item-sequence";

    @Id
    private long id;
    private long inventoryItemId;
    private int quantity;


    @Override
    public int hashCode() {
        return Objects.hash(inventoryItemId, quantity, id);
    }
}
