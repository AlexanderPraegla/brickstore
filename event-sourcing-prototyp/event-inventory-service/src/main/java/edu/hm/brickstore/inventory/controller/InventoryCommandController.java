package edu.hm.brickstore.inventory.controller;

import edu.hm.brickstore.inventory.dto.UpdateInventoryItemStatusDTO;
import edu.hm.brickstore.inventory.dto.UpdateInventoryItemsStockDTO;
import edu.hm.brickstore.inventory.entity.InventoryItem;
import edu.hm.brickstore.inventory.service.InventoryCommandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "inventory", produces = {"application/json"})
@Tag(name = "Inventory command API")
public class InventoryCommandController {

    @Value("${spring.gateway.host}")
    private String host;
    @Value("${spring.gateway.port}")
    private String port;
    @Value("${spring.gateway.scheme}")
    private String scheme;

    private final InventoryCommandService inventoryCommandService;

    public InventoryCommandController(InventoryCommandService inventoryCommandService) {
        this.inventoryCommandService = inventoryCommandService;
    }

    @PreAuthorize("hasAuthority('admins')")
    @PutMapping
    public ResponseEntity<?> createInventoryItem(UriComponentsBuilder b, @Valid @RequestBody InventoryItem inventoryItem) {
        InventoryItem account = inventoryCommandService.createInventoryItem(inventoryItem);

        UriComponents uriComponents = b.scheme(scheme).host(host).port(port).path("/inventory/{inventoryItemId}").buildAndExpand(account.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PreAuthorize("hasAuthority('admins')")
    @PostMapping("/{inventoryItemId}")
    public ResponseEntity<?> updateInventoryItem(@PathVariable long inventoryItemId, @Valid @RequestBody InventoryItem inventoryItem) {
        inventoryCommandService.updateInventoryItem(inventoryItemId, inventoryItem);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('admins')")
    @PostMapping("/{inventoryItemId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable long inventoryItemId, @Valid @RequestBody UpdateInventoryItemStatusDTO updateInventoryItemStatusDTO) {
        inventoryCommandService.updateStatus(inventoryItemId, updateInventoryItemStatusDTO);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('admins')")
    @PostMapping("/gather")
    public ResponseEntity<?> gather(@Valid @RequestBody UpdateInventoryItemsStockDTO gatherInventoryItemsDTO) {
        inventoryCommandService.gatherInventoryItem(gatherInventoryItemsDTO);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('admins')")
    @PostMapping("/stockup")
    public ResponseEntity<?> stockup(@Valid @RequestBody UpdateInventoryItemsStockDTO stockInventoryItemsDTO) {
        inventoryCommandService.stockUpInventoryItem(stockInventoryItemsDTO);
        return ResponseEntity.ok().build();
    }


}
