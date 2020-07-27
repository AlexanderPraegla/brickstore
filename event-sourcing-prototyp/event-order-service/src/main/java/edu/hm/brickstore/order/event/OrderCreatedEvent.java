package edu.hm.brickstore.order.event;

import edu.hm.brickstore.order.entity.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class OrderCreatedEvent extends Event<Order> {

    private Order payload;

    public OrderCreatedEvent(long aggregateId, Order payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public Order getPayload() {
        return payload;
    }
}
