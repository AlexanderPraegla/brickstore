package edu.hm.brickstore.inventory.event;

import edu.hm.brickstore.inventory.dto.UpdateInventoryItemStatusDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
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
