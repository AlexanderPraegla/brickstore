package edu.hm.praegla.inventory.event;

import edu.hm.praegla.inventory.entity.InventoryItem;
import edu.hm.praegla.shoppingcart.event.Event;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventoryItemUpdatedEvent extends Event<InventoryItem> {

    private InventoryItem payload;

    public InventoryItemUpdatedEvent(long aggregateId, InventoryItem payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public InventoryItem getPayload() {
        return payload;
    }
}
