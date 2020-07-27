package edu.hm.brickstore.inventory;

import edu.hm.brickstore.BrickstoreRestTest;
import edu.hm.brickstore.client.InventoryTestTestClient;
import edu.hm.brickstore.error.dto.ApiErrorDTO;
import edu.hm.brickstore.inventory.dto.InventoryItemDTO;
import edu.hm.brickstore.parameterResolver.InventoryItemParameterResolver;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InventoryItemParameterResolver.class)
public class InventoryTest extends BrickstoreRestTest {

    private final InventoryTestTestClient inventoryTestClient;
    private InventoryItemDTO testInventoryItem;

    public InventoryTest() {
        this.inventoryTestClient = new InventoryTestTestClient(spec);
    }

    @BeforeEach
    public void beforeEach(InventoryItemDTO inventoryItemDTO) {
        testInventoryItem = inventoryTestClient.createInventoryItem(inventoryItemDTO);
    }

    @Test
    @Order(1)
    public void shouldCreateNewInventoryItem() {
        InventoryItemDTO inventoryItemDTO = new InventoryItemDTO();
        inventoryItemDTO.setName("Star Wars - Todesstern");
        inventoryItemDTO.setPrice(new BigDecimal("999.99"));
        inventoryItemDTO.setStock(2);
        inventoryItemDTO.setDeliveryTime(8);
        inventoryItemDTO.setStatus("AVAILABLE");

        InventoryItemDTO createdInventoryItem = inventoryTestClient.createInventoryItem(inventoryItemDTO);
        assertThat(createdInventoryItem).isEqualToIgnoringGivenFields(inventoryItemDTO, "id");
    }

    @Test
    @Order(2)
    public void shouldModifyInventoryItem() {
        testInventoryItem.setName("Harry Potter - Große Besenkammer");
        testInventoryItem.setPrice(new BigDecimal("9.99"));
        testInventoryItem.setStock(200);
        testInventoryItem.setDeliveryTime(1);
        testInventoryItem.setStatus("AVAILABLE");

        inventoryTestClient.updateInventoryItem(testInventoryItem);

        InventoryItemDTO updatedInventoryItem = inventoryTestClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(updatedInventoryItem).isEqualTo(testInventoryItem);
    }

    @Test
    @Order(3)
    public void shouldDeactivateInventoryItem() {
        inventoryTestClient.updateInventoryItemStatus(testInventoryItem.getId(), "DEACTIVATED")
                .then()
                .statusCode(200);

        InventoryItemDTO item = inventoryTestClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(item.getStatus()).isEqualTo("DEACTIVATED");
    }

    @Test
    @Order(4)
    public void shouldFindAllItemsMatchingSearchName() {
        testInventoryItem.setName("Harry Potter - Winkelgasse");
        inventoryTestClient.updateInventoryItem(testInventoryItem);

        String searchTerm = "Winkelgasse";
        int resultCount = 1;

        List<InventoryItemDTO> items = inventoryTestClient.searchInventoryItem(searchTerm);
        assertThat(items).isNotEmpty();
    }

    @Test
    public void shouldGetAllAvailable() {
        List<InventoryItemDTO> items = inventoryTestClient.getAvailableInventoryItems();
        assertThat(items).isNotEmpty();
        assertThat(items.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void shouldGatherInventoryItemWithEnoughStock() {
        int newStock = 3;
        int gatherQuantity = 2;
        int expectedStock = 1;

        testInventoryItem.setStock(newStock);
        inventoryTestClient.updateInventoryItem(testInventoryItem);

        Response response = inventoryTestClient.gatherInventoryItem(testInventoryItem.getId(), gatherQuantity);
        response.then()
                .statusCode(200);

        testInventoryItem = inventoryTestClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(testInventoryItem.getStock()).isEqualTo(expectedStock);
    }

    @Test
    public void shouldGatherNegativeQuantityInventoryItem() {
        Response response = inventoryTestClient.gatherInventoryItem(testInventoryItem.getId(), -1);
        response.then()
                .statusCode(400);
    }

    @Test
    public void shouldGatherInventoryItemWithNotEnoughStock() {
        int newStock = 1;
        int gatherQuantity = 2;

        testInventoryItem.setStock(newStock);
        inventoryTestClient.updateInventoryItem(testInventoryItem);

        Response response = inventoryTestClient.gatherInventoryItem(testInventoryItem.getId(), gatherQuantity);
        ApiErrorDTO apiErrorDTO = response.then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("NOT_ENOUGH_STOCK");

    }

    @Test
    public void shouldGatherCompleteStockOfInventoryItem() {
        int gatherQuantity = 5;

        Response response = inventoryTestClient.gatherInventoryItem(testInventoryItem.getId(), gatherQuantity);
        response.then()
                .statusCode(200);

        testInventoryItem = inventoryTestClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(testInventoryItem.getStock()).isEqualTo(0);
        assertThat(testInventoryItem.getStatus()).isEqualTo("OUT_OF_STOCK");

    }

    @Test
    public void shouldGatherOutOfStockInventoryItem() {
        int newStock = 0;
        int gatherQuantity = 2;

        testInventoryItem.setStock(newStock);
        testInventoryItem.setStatus("OUT_OF_STOCK");
        inventoryTestClient.updateInventoryItem(testInventoryItem);

        Response response = inventoryTestClient.gatherInventoryItem(testInventoryItem.getId(), gatherQuantity);
        ApiErrorDTO apiErrorDTO = response.then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("OUT_OF_STOCK");

    }

    @Test
    public void shouldGatherDeactivatedInventoryItem() {
        int newStock = 3;
        int gatherQuantity = 2;

        testInventoryItem.setStock(newStock);
        testInventoryItem.setStatus("DEACTIVATED");
        inventoryTestClient.updateInventoryItem(testInventoryItem);

        Response response = inventoryTestClient.gatherInventoryItem(testInventoryItem.getId(), gatherQuantity);
        ApiErrorDTO apiErrorDTO = response.then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ITEM_NOT_ORDERABLE");

    }

    @Test
    public void shouldStockUpInventoryItem() {
        Response response = inventoryTestClient.stockUpInventoryItem(testInventoryItem.getId(), 10);
        response.then()
                .statusCode(200);

        testInventoryItem = inventoryTestClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(testInventoryItem.getStock()).isEqualTo(15);
        assertThat(testInventoryItem.getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    public void shouldStockUpInventoryItemOutOfStock() {
        int newStock = 0;
        int stockUpQuantity = 2;

        testInventoryItem.setStock(newStock);
        testInventoryItem.setStatus("OUT_OF_STOCK");
        inventoryTestClient.updateInventoryItem(testInventoryItem);

        testInventoryItem = inventoryTestClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(testInventoryItem.getStock()).isEqualTo(0);
        assertThat(testInventoryItem.getStatus()).isEqualTo("OUT_OF_STOCK");

        Response response = inventoryTestClient.stockUpInventoryItem(testInventoryItem.getId(), stockUpQuantity);
        response.then()
                .statusCode(200);

        InventoryItemDTO item = inventoryTestClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(item.getStock()).isEqualTo(2);
        assertThat(item.getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    public void shouldStockUpDeactivatedInventoryItem() {
        testInventoryItem.setStatus("DEACTIVATED");
        inventoryTestClient.updateInventoryItem(testInventoryItem);

        int stockUpQuantity = 3;
        int expectedStock = 8;

        Response response = inventoryTestClient.stockUpInventoryItem(testInventoryItem.getId(), stockUpQuantity);
        response.then()
                .statusCode(200);

        InventoryItemDTO item = inventoryTestClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(item.getStock()).isEqualTo(expectedStock);
        assertThat(item.getStatus()).isEqualTo("DEACTIVATED");
    }

}
