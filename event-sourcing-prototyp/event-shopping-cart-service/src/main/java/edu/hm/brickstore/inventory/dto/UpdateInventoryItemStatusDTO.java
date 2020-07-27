package edu.hm.brickstore.inventory.dto;

import edu.hm.brickstore.inventory.entity.InventoryItemStatus;
import lombok.Data;

@Data
public class UpdateInventoryItemStatusDTO {
    private InventoryItemStatus status;
}
