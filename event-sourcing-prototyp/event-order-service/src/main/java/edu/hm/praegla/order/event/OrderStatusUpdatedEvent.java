package edu.hm.praegla.order.event;

import edu.hm.praegla.order.dto.UpdateOrderStatusDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderStatusUpdatedEvent extends Event<UpdateOrderStatusDTO> {

    private UpdateOrderStatusDTO payload;

    public OrderStatusUpdatedEvent(long aggregateId, UpdateOrderStatusDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public UpdateOrderStatusDTO getPayload() {
        return payload;
    }
}
