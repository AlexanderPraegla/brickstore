package edu.hm.praegla.order.event;

import edu.hm.praegla.order.dto.OrderErrorDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDebitAccountFailedEvent extends Event<OrderErrorDTO> {

    private OrderErrorDTO payload;

    public OrderDebitAccountFailedEvent(long aggregateId, OrderErrorDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public OrderErrorDTO getPayload() {
        return payload;
    }
}
