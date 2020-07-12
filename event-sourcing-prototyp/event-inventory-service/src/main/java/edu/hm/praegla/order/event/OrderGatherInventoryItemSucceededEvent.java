package edu.hm.praegla.order.event;

import edu.hm.praegla.inventory.event.Event;
import edu.hm.praegla.order.entity.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderGatherInventoryItemSucceededEvent extends Event<Order> {

    private Order payload;

    public OrderGatherInventoryItemSucceededEvent(long aggregateId, Order payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public Order getPayload() {
        return payload;
    }
}
