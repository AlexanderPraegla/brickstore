package edu.hm.praegla.order.event;

import edu.hm.praegla.order.entity.Order;
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
