package edu.hm.brickstore.order.event;

import edu.hm.brickstore.order.entity.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
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
