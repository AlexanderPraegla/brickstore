package edu.hm.praegla.inventory.controller;

import edu.hm.praegla.inventory.entity.InventoryItem;
import edu.hm.praegla.inventory.entity.InventoryItemStatus;
import edu.hm.praegla.inventory.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping(value = "inventory", produces = {"application/json"})
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @GetMapping
    public Iterable<InventoryItem> getInventoryItems() {
        return inventoryService.getInventoryItems();
    }

    @GetMapping("available")
    public Iterable<InventoryItem> getAvailableInventoryItems() {
        return inventoryService.getAvailableInventoryItems();
    }

    @GetMapping("search")
    public Iterable<InventoryItem> searchInventoryItems(@RequestParam("name") String name) {
        return inventoryService.searchInventoryItems(name);
    }

    @GetMapping("/{inventoryItemId}")
    public InventoryItem getInventoryItem(@PathVariable long inventoryItemId) {
        return inventoryService.getInventoryItem(inventoryItemId);
    }

    @PutMapping
    public ResponseEntity<?> createInventoryItem(UriComponentsBuilder b, @Valid @RequestBody InventoryItem inventoryItem) {
        InventoryItem account = inventoryService.createInventoryItem(inventoryItem);

        UriComponents uriComponents = b.path("/inventory/{inventoryItemId}").buildAndExpand(account.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }


    @PostMapping("/{inventoryItemId}")
    public ResponseEntity<?> updateInventoryItem(@PathVariable long inventoryItemId, @Valid @RequestBody InventoryItem inventoryItem) {
        inventoryService.updateInventoryItem(inventoryItemId, inventoryItem);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{inventoryItemId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable long inventoryItemId, @Valid @RequestBody UpdateInventoryItemStatusDTO updateInventoryItemStatusDTO) {
        inventoryService.updateStatus(inventoryItemId, updateInventoryItemStatusDTO.status);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/gather")
    public ResponseEntity<?> gather(@Valid @RequestBody InventoryController.ChangeInventoryItemStockDTO changeInventoryItemStockDTO) {
        inventoryService.gatherInventoryItem(changeInventoryItemStockDTO.inventoryItemId, changeInventoryItemStockDTO.quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stockup")
    public ResponseEntity<?> stockup(@Valid @RequestBody InventoryController.ChangeInventoryItemStockDTO changeInventoryItemStockDTO) {
        inventoryService.stockUpInventoryItem(changeInventoryItemStockDTO.inventoryItemId, changeInventoryItemStockDTO.quantity);
        return ResponseEntity.ok().build();
    }


    private static class UpdateInventoryItemStatusDTO {
        public InventoryItemStatus status;
    }

    private static class ChangeInventoryItemStockDTO {
        public long inventoryItemId;
        @Min(1)
        public int quantity;
    }
}
