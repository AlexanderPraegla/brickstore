package edu.hm.brickstore.shoppingcart.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "shopping_cart")
public class ShoppingCart {

    @Transient
    public static final String SEQUENCE_NAME = "shopping-cart-sequence";

    @Id
    private long id;
    @Min(1)
    private long accountId;
    @NotNull
    private String customerName;
    private Set<LineItem> lineItems;

    public void addLineItem(LineItem lineItem) {
        if (lineItems == null) {
            lineItems = new HashSet<>();
        }
        lineItems.add(lineItem);
    }

    public void removeLineItem(long lineItemId) {
        if (lineItems != null && lineItems.size() > 0) {
            lineItems.removeIf(lineItem -> lineItem.getId() == lineItemId);
        }
    }
}
