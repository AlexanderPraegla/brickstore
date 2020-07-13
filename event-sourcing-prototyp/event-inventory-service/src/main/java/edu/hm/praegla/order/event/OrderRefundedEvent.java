package edu.hm.praegla.order.event;

import edu.hm.praegla.inventory.event.Event;
import edu.hm.praegla.order.entity.Order;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderRefundedEvent extends Event<Order> {

    private Order payload;

    public OrderRefundedEvent(long aggregateId, Order payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public Order getPayload() {
        return payload;
    }
}
