package edu.hm.praegla.inventory.event;

import edu.hm.praegla.inventory.dto.UpdateInventoryItemStatusDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventoryItemStatusUpdatedEvent extends Event<UpdateInventoryItemStatusDTO> {

    private UpdateInventoryItemStatusDTO payload;

    public InventoryItemStatusUpdatedEvent(long aggregateId, UpdateInventoryItemStatusDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public UpdateInventoryItemStatusDTO getPayload() {
        return payload;
    }
}
