package edu.hm.praegla.inventory.controller;

import edu.hm.praegla.inventory.entity.InventoryItem;
import edu.hm.praegla.inventory.service.InventoryQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "inventory", produces = {"application/json"})
public class InventoryQueryController {

    private final InventoryQueryService inventoryQueryService;

    public InventoryQueryController(InventoryQueryService inventoryQueryService) {
        this.inventoryQueryService = inventoryQueryService;
    }

    @GetMapping
    public Iterable<InventoryItem> getInventoryItems() {
        return inventoryQueryService.getInventoryItems();
    }

    @GetMapping("available")
    public Iterable<InventoryItem> getAvailableInventoryItems() {
        return inventoryQueryService.getAvailableInventoryItems();
    }

    @GetMapping("search")
    public Iterable<InventoryItem> searchInventoryItems(@RequestParam("name") String name) {
        return inventoryQueryService.searchInventoryItems(name);
    }

    @GetMapping("/{inventoryItemId}")
    public InventoryItem getInventoryItem(@PathVariable long inventoryItemId) {
        return inventoryQueryService.getInventoryItem(inventoryItemId);
    }
}
