package edu.hm.praegla.inventory.dto;

import edu.hm.praegla.inventory.entity.InventoryItemStatus;
import lombok.Data;

@Data
public class UpdateInventoryItemStatusDTO {
    private InventoryItemStatus status;
}
