package edu.hm.praegla.client.inventory;

import edu.hm.praegla.client.inventory.dto.ChangeInventoryItemStockDTO;
import edu.hm.praegla.client.inventory.dto.InventoryItemDTO;
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

    @RequestMapping(method = RequestMethod.POST, value = "/inventory/gather")
    void gather(@Valid @RequestBody ChangeInventoryItemStockDTO changeInventoryItemStockDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/inventory/stockup")
    void stockUp(@Valid @RequestBody ChangeInventoryItemStockDTO changeInventoryItemStockDTO);


}
