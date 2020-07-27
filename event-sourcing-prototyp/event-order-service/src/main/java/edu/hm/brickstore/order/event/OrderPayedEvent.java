package edu.hm.brickstore.order.event;

import edu.hm.brickstore.order.entity.Order;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderPayedEvent extends Event<Order> {

    private Order payload;

    public OrderPayedEvent(long aggregateId, Order payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public Order getPayload() {
        return payload;
    }
}
