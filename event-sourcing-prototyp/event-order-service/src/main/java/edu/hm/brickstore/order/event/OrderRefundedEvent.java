package edu.hm.brickstore.order.event;

import edu.hm.brickstore.order.entity.Order;
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
