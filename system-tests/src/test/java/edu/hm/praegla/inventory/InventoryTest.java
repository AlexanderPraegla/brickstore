package edu.hm.praegla.inventory;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.error.dto.ApiErrorDTO;
import edu.hm.praegla.inventory.dto.InventoryItemDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryTest extends BrickstoreRestTest {

    @Test
    @Order(1)
    public void shouldGetAllAvailableInventoryItems() {
        List<InventoryItemDTO> items = given(spec)
                .when()
                .get("inventory/available")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList(".", InventoryItemDTO.class);
        assertThat(items).hasSize(13);
    }

    @ParameterizedTest
    @CsvSource({"potter,5", "schiff,2", ",18"})
    @Order(2)
    public void shouldFindAllItemsMatchingSearchName(String searchTerm, int resultCount) {
        List<InventoryItemDTO> items = given(spec)
                .when()
                .queryParam("name", searchTerm)
                .get("inventory/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList(".", InventoryItemDTO.class);
        assertThat(items).hasSize(resultCount);
    }

    @Test
    public void shouldCreateNewInventoryItem() {
        InventoryItemDTO inventoryItemDTO = new InventoryItemDTO();
        inventoryItemDTO.setName("Star Wars - Todesstern");
        inventoryItemDTO.setPrice(999.99);
        inventoryItemDTO.setStock(2);
        inventoryItemDTO.setDeliveryTime(8);
        inventoryItemDTO.setStatus("AVAILABLE");

        InventoryItemDTO createdInventoryItem = createInventoryItem(inventoryItemDTO);
        assertThat(createdInventoryItem).isEqualToIgnoringGivenFields(inventoryItemDTO, "id");
    }

    @Test
    public void shouldModifyInventoryItem() {
        InventoryItemDTO inventoryItemDTO = new InventoryItemDTO();
        inventoryItemDTO.setId(1);
        inventoryItemDTO.setName("Harry Potter - Große Besenkammer");
        inventoryItemDTO.setPrice(9.99);
        inventoryItemDTO.setStock(200);
        inventoryItemDTO.setDeliveryTime(1);
        inventoryItemDTO.setStatus("AVAILABLE");

        given(spec)
                .when()
                .body(inventoryItemDTO)
                .post("inventory/{inventoryItemId}", inventoryItemDTO.getId())
                .then()
                .statusCode(200);

        InventoryItemDTO updatedInventoryItem = getInventoryItemById(inventoryItemDTO.getId());
        assertThat(updatedInventoryItem).isEqualTo(inventoryItemDTO);
    }

    @Test
    public void shouldDeactivateInventoryItem() {
        Map<String, String> body = new HashMap<>();
        body.put("status", "DEACTIVATED");
        given(spec)
                .when()
                .body(body)
                .post("inventory/{inventoryItemId}/status", 2)
                .then()
                .statusCode(200);

        InventoryItemDTO item = getInventoryItemById(2);
        assertThat(item.getStatus()).isEqualTo("DEACTIVATED");
    }

    @Test
    public void shouldGatherInventoryItemWithEnoughStock() {
        int inventoryItemId = 4;
        InventoryItemDTO item = getInventoryItemById(inventoryItemId);
        assertThat(item.getStock()).isEqualTo(3);

        Response response = gatherInventoryItem(inventoryItemId, 2);
        response.then()
                .statusCode(200);

        item = getInventoryItemById(inventoryItemId);
        assertThat(item.getStock()).isEqualTo(1);
    }

    @Test
    public void shouldGatherNegativeQuantityInventoryItem() {
        int inventoryItemId = 6;

        Response response = gatherInventoryItem(inventoryItemId, -1);
        response.then()
                .statusCode(400);
    }

    @Test
    public void shouldGatherInventoryItemWithNotEnoughStock() {
        int inventoryItemId = 7;

        Response response = gatherInventoryItem(inventoryItemId, 2);
        ApiErrorDTO apiErrorDTO = response.then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("NOT_ENOUGH_STOCK");

    }

    @Test
    public void shouldGatherCompleteStockOfInventoryItem() {
        int inventoryItemId = 9;
        InventoryItemDTO item = getInventoryItemById(inventoryItemId);
        assertThat(item.getStock()).isEqualTo(2);

        Response response = gatherInventoryItem(inventoryItemId, 2);
        response.then()
                .statusCode(200);

        item = getInventoryItemById(inventoryItemId);
        assertThat(item.getStock()).isEqualTo(0);
        assertThat(item.getStatus()).isEqualTo("OUT_OF_STOCK");

    }

    @Test
    public void shouldGatherOutOfStockInventoryItem() {
        int inventoryItemId = 3;

        Response response = gatherInventoryItem(inventoryItemId, 2);
        ApiErrorDTO apiErrorDTO = response.then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("OUT_OF_STOCK");

    }

    @Test
    public void shouldGatherDeactivatedInventoryItem() {
        int inventoryItemId = 5;

        Response response = gatherInventoryItem(inventoryItemId, 2);
        ApiErrorDTO apiErrorDTO = response.then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ITEM_NOT_ORDERABLE");

    }

    @Test
    public void shouldStockUpInventoryItem() {
        int inventoryItemId = 10;
        InventoryItemDTO item = getInventoryItemById(inventoryItemId);
        assertThat(item.getStock()).isEqualTo(1);
        Response response = stockUpInventoryItem(inventoryItemId, 10);
        response.then()
                .statusCode(200);

        item = getInventoryItemById(inventoryItemId);
        assertThat(item.getStock()).isEqualTo(11);
        assertThat(item.getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    public void shouldStockUpInventoryItemOutOfStock() {
        int inventoryItemId = 8;
        Response response = stockUpInventoryItem(inventoryItemId, 2);
        response.then()
                .statusCode(200);

        InventoryItemDTO item = getInventoryItemById(inventoryItemId);
        assertThat(item.getStock()).isEqualTo(2);
        assertThat(item.getStatus()).isEqualTo("AVAILABLE");
    }

    private InventoryItemDTO getInventoryItemById(long inventoryItemId) {
        return getResourceById("inventory/{inventoryItemId}", inventoryItemId, InventoryItemDTO.class);
    }

    private InventoryItemDTO createInventoryItem(InventoryItemDTO inventoryItemDTO) {
        String inventoryItemLocation = createResource("inventory", inventoryItemDTO);
        return getResourceByLocationHeader(inventoryItemLocation, InventoryItemDTO.class);
    }

    private Response gatherInventoryItem(long inventoryItemId, int quantity) {
        Map<String, Number> body = new HashMap<>();
        body.put("inventoryItemId", inventoryItemId);
        body.put("quantity", quantity);
        return given(spec)
                .when()
                .body(body)
                .post("inventory/gather");
    }

    private Response stockUpInventoryItem(long inventoryItemId, int quantity) {
        Map<String, Number> body = new HashMap<>();
        body.put("inventoryItemId", inventoryItemId);
        body.put("quantity", quantity);
        return given(spec)
                .when()
                .body(body)
                .post("inventory/stockup");
    }

}
