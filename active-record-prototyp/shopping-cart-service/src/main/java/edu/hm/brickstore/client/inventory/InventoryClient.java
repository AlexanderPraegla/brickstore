package edu.hm.brickstore.client.inventory;

import edu.hm.brickstore.client.inventory.dto.InventoryItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "inventory-service")
public interface InventoryClient {

    @RequestMapping(method = RequestMethod.GET, value = "/inventory/{inventoryItemId}")
    InventoryItemDTO getInventoryItem(@PathVariable long inventoryItemId);

}
