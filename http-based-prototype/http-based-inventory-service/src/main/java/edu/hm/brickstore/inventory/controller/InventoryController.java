package edu.hm.brickstore.inventory.controller;

import edu.hm.brickstore.inventory.dto.UpdateInventoryItemsStockDTO;
import edu.hm.brickstore.inventory.entity.InventoryItem;
import edu.hm.brickstore.inventory.entity.InventoryItemStatus;
import edu.hm.brickstore.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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

@Slf4j
@Validated
@RestController
@RequestMapping(value = "inventory", produces = {"application/json"})
@Tag(name = "Inventory API")
public class InventoryController {

    @Value("${spring.gateway.host}")
    private String host;
    @Value("${spring.gateway.port}")
    private String port;
    @Value("${spring.gateway.scheme}")
    private String scheme;

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @PreAuthorize("hasAuthority('admins')")
    @GetMapping
    public Iterable<InventoryItem> getInventoryItems() {
        return inventoryService.getInventoryItems();
    }

    @PreAuthorize("hasAuthority('customers')")
    @GetMapping("available")
    public Iterable<InventoryItem> getAvailableInventoryItems() {
        return inventoryService.getAvailableInventoryItems();
    }

    @PreAuthorize("hasAuthority('customers')")
    @GetMapping("search")
    public Iterable<InventoryItem> searchInventoryItems(@RequestParam("name") String name) {
        return inventoryService.searchInventoryItems(name);
    }

    @PreAuthorize("hasAuthority('customers')")
    @GetMapping("/{inventoryItemId}")
    public InventoryItem getInventoryItem(@PathVariable long inventoryItemId) {
        return inventoryService.getInventoryItem(inventoryItemId);
    }

    @PreAuthorize("hasAuthority('admins')")
    @PutMapping
    public ResponseEntity<?> createInventoryItem(UriComponentsBuilder b, @Valid @RequestBody InventoryItem inventoryItem) {
        InventoryItem account = inventoryService.createInventoryItem(inventoryItem);

        UriComponents uriComponents = b.scheme(scheme).host(host).port(port).path("/inventory/{inventoryItemId}").buildAndExpand(account.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PreAuthorize("hasAuthority('admins')")
    @PostMapping("/{inventoryItemId}")
    public ResponseEntity<?> updateInventoryItem(@PathVariable long inventoryItemId, @Valid @RequestBody InventoryItem inventoryItem) {
        inventoryService.updateInventoryItem(inventoryItemId, inventoryItem);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('admins')")
    @PostMapping("/{inventoryItemId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable long inventoryItemId, @Valid @RequestBody UpdateInventoryItemStatusDTO updateInventoryItemStatusDTO) {
        inventoryService.updateStatus(inventoryItemId, updateInventoryItemStatusDTO.status);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('admins')")
    @PostMapping("/gather")
    public ResponseEntity<?> gather(@Valid @RequestBody UpdateInventoryItemsStockDTO changeInventoryItemStockDTO) {
        inventoryService.gatherInventoryItem(changeInventoryItemStockDTO);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('admins')")
    @PostMapping("/stockup")
    public ResponseEntity<?> stockup(@Valid @RequestBody UpdateInventoryItemsStockDTO changeInventoryItemStockDTO) {
        inventoryService.stockUpInventoryItem(changeInventoryItemStockDTO);
        return ResponseEntity.ok().build();
    }


    private static class UpdateInventoryItemStatusDTO {
        public InventoryItemStatus status;
    }

}
