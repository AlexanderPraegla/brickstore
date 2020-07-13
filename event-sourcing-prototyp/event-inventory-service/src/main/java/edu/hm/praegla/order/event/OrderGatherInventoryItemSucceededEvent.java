package edu.hm.praegla.order.event;

import edu.hm.praegla.inventory.event.Event;
import edu.hm.praegla.order.dto.OrderStatusUpdateDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderGatherInventoryItemSucceededEvent extends Event<OrderStatusUpdateDTO> {

    private OrderStatusUpdateDTO payload;

    public OrderGatherInventoryItemSucceededEvent(long aggregateId, OrderStatusUpdateDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public OrderStatusUpdateDTO getPayload() {
        return payload;
    }
}
