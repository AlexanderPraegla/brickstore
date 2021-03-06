package edu.hm.brickstore.client.inventory;

import edu.hm.brickstore.client.inventory.dto.InventoryItemDTO;
import edu.hm.brickstore.client.inventory.dto.UpdateInventoryItemsStockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@FeignClient(value = "inventory-service")
public interface InventoryClient {

    @RequestMapping(method = RequestMethod.GET, value = "/inventory/{inventoryItemId}")
    InventoryItemDTO getInventoryItem(@PathVariable long inventoryItemId);

    @RequestMapping(method = RequestMethod.GET, value = "/actuator/health")
    void health();

    @RequestMapping(method = RequestMethod.POST, value = "/inventory/gather")
    void gather(@Valid @RequestBody UpdateInventoryItemsStockDTO changeInventoryItemStockDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/inventory/stockup")
    void stockUp(@Valid @RequestBody UpdateInventoryItemsStockDTO changeInventoryItemStockDTO);


}
