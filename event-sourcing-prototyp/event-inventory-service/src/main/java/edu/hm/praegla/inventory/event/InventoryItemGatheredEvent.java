package edu.hm.praegla.inventory.event;

import edu.hm.praegla.inventory.dto.UpdateInventoryItemsStockDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class InventoryItemGatheredEvent extends Event<UpdateInventoryItemsStockDTO.Item> {

    private UpdateInventoryItemsStockDTO.Item payload;

    public InventoryItemGatheredEvent(long aggregateId, UpdateInventoryItemsStockDTO.Item payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public UpdateInventoryItemsStockDTO.Item getPayload() {
        return payload;
    }
}
