package edu.hm.praegla.order.event;

import edu.hm.praegla.order.dto.OrderErrorDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
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
