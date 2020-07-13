package edu.hm.praegla.order.event;

import edu.hm.praegla.order.dto.OrderErrorDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderReturnInventoryItemFailedEvent extends Event<OrderErrorDTO> {

    private OrderErrorDTO payload;

    public OrderReturnInventoryItemFailedEvent(long aggregateId, OrderErrorDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public OrderErrorDTO getPayload() {
        return payload;
    }
}
