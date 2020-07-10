package edu.hm.praegla.client;

import edu.hm.praegla.ApiClient;
import edu.hm.praegla.inventory.dto.ChangeInventoryItemStockDTO;
import edu.hm.praegla.inventory.dto.InventoryItemDTO;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class InventoryClient extends ApiClient {

    public InventoryClient(RequestSpecification spec) {
        super(spec);
    }

    public InventoryItemDTO getInventoryItemById(long inventoryItemId) {
        return getResourceById("inventory/{inventoryItemId}", inventoryItemId, InventoryItemDTO.class);
    }

    public List<InventoryItemDTO> getInventoryItems() {
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
        ChangeInventoryItemStockDTO item = new ChangeInventoryItemStockDTO(inventoryItemId, quantity);
        List<ChangeInventoryItemStockDTO> items = Collections.singletonList(item);
        return given(spec)
                .when()
                .body(items)
                .post("inventory/gather");
    }

    public Response stockUpInventoryItem(long inventoryItemId, int quantity) {
        ChangeInventoryItemStockDTO item = new ChangeInventoryItemStockDTO(inventoryItemId, quantity);
        List<ChangeInventoryItemStockDTO> items = Collections.singletonList(item);
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
