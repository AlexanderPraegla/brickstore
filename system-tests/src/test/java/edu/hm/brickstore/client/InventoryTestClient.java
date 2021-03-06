package edu.hm.brickstore.client;

import edu.hm.brickstore.inventory.dto.InventoryItemDTO;
import edu.hm.brickstore.inventory.dto.UpdateInventoryItemsStockDTO;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class InventoryTestClient extends ApiTestClient {

    public InventoryTestClient(RequestSpecification spec) {
        super(spec);
    }

    public InventoryItemDTO getInventoryItemById(long inventoryItemId) {
        return getResourceById("inventory/{inventoryItemId}", inventoryItemId, InventoryItemDTO.class);
    }

    public List<InventoryItemDTO> getAvailableInventoryItems() {
        return given(spec)
                .when()
                .get("inventory/available")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList(".", InventoryItemDTO.class);
    }

    public InventoryItemDTO createInventoryItem(InventoryItemDTO inventoryItemDTO) {
        String inventoryItemLocation = createResource("inventory", inventoryItemDTO);
        return getResourceByLocationHeader(inventoryItemLocation, InventoryItemDTO.class);
    }

    public void updateInventoryItem(InventoryItemDTO inventoryItemDTO) {
        given(spec)
                .when()
                .body(inventoryItemDTO)
                .post("inventory/{inventoryItemId}", inventoryItemDTO.getId())
                .then()
                .statusCode(200);
    }

    public Response updateInventoryItemStatus(long inventoryItemId, String status) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);
        return given(spec)
                .when()
                .body(body)
                .post("inventory/{inventoryItemId}/status", inventoryItemId);
    }

    public Response gatherInventoryItem(long inventoryItemId, int quantity) {
        UpdateInventoryItemsStockDTO.Item item = new UpdateInventoryItemsStockDTO.Item(inventoryItemId, quantity);
        UpdateInventoryItemsStockDTO items = new UpdateInventoryItemsStockDTO();
        items.setItems(Collections.singletonList(item));
        return given(spec)
                .when()
                .body(items)
                .post("inventory/gather");
    }

    public Response stockUpInventoryItem(long inventoryItemId, int quantity) {
        UpdateInventoryItemsStockDTO.Item item = new UpdateInventoryItemsStockDTO.Item(inventoryItemId, quantity);
        UpdateInventoryItemsStockDTO items = new UpdateInventoryItemsStockDTO();
        items.setItems(Collections.singletonList(item));
        return given(spec)
                .when()
                .body(items)
                .post("inventory/stockup");
    }

    public List<InventoryItemDTO> searchInventoryItem(String searchTerm) {
        return given(spec)
                .when()
                .queryParam("name", searchTerm)
                .get("inventory/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList(".", InventoryItemDTO.class);
    }

}
