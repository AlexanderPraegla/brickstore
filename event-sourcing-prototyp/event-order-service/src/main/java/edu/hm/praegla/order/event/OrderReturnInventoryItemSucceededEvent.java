package edu.hm.praegla.order.event;

import edu.hm.praegla.order.dto.OrderStatusUpdateDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class OrderReturnInventoryItemSucceededEvent extends Event<OrderStatusUpdateDTO> {

    private OrderStatusUpdateDTO payload;

    public OrderReturnInventoryItemSucceededEvent(long aggregateId, OrderStatusUpdateDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public OrderStatusUpdateDTO getPayload() {
        return payload;
    }
}
