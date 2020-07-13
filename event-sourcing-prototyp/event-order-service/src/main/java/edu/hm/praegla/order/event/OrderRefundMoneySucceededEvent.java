package edu.hm.praegla.order.event;

import edu.hm.praegla.order.dto.OrderStatusUpdateDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderRefundMoneySucceededEvent extends Event<OrderStatusUpdateDTO> {

    private OrderStatusUpdateDTO payload;

    public OrderRefundMoneySucceededEvent(long aggregateId, OrderStatusUpdateDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public OrderStatusUpdateDTO getPayload() {
        return payload;
    }
}
