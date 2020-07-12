package edu.hm.praegla.order.event;

import edu.hm.praegla.inventory.event.Event;
import edu.hm.praegla.order.entity.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderCanceledEvent extends Event<Order> {

    private Order payload;

    public OrderCanceledEvent(long aggregateId, Order order) {
        super(aggregateId);
        this.payload = order;
    }

    @Override
    public Order getPayload() {
        return payload;
    }
}
