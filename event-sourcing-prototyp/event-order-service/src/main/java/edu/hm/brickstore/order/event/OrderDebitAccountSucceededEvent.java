package edu.hm.brickstore.order.event;

import edu.hm.brickstore.order.dto.OrderStatusUpdateDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderDebitAccountSucceededEvent extends Event<OrderStatusUpdateDTO> {

    private OrderStatusUpdateDTO payload;

    public OrderDebitAccountSucceededEvent(long aggregateId, OrderStatusUpdateDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public OrderStatusUpdateDTO getPayload() {
        return payload;
    }
}
