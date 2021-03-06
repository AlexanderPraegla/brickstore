package edu.hm.brickstore.inventory.event;

import edu.hm.brickstore.inventory.entity.InventoryItem;
import edu.hm.brickstore.shoppingcart.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class InventoryItemCreatedEvent extends Event<InventoryItem> {

    private InventoryItem payload;

    public InventoryItemCreatedEvent(long aggregateId, InventoryItem payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public InventoryItem getPayload() {
        return payload;
    }
}
