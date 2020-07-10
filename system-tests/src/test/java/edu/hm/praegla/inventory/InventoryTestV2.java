package edu.hm.praegla.inventory;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.client.InventoryClient;
import edu.hm.praegla.error.dto.ApiErrorDTO;
import edu.hm.praegla.inventory.dto.InventoryItemDTO;
import edu.hm.praegla.parameterResolver.InventoryItemParameterResolver;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InventoryItemParameterResolver.class)
public class InventoryTestV2 extends BrickstoreRestTest {

    private final InventoryClient inventoryClient;
    private InventoryItemDTO testInventoryItem;

    public InventoryTestV2() {
        this.inventoryClient = new InventoryClient(spec);
    }

    @BeforeEach
    public void beforeEach(InventoryItemDTO inventoryItemDTO) {
        testInventoryItem = inventoryClient.createInventoryItem(inventoryItemDTO);
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

        InventoryItemDTO createdInventoryItem = inventoryClient.createInventoryItem(inventoryItemDTO);
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

        inventoryClient.updateInventoryItem(testInventoryItem);

        InventoryItemDTO updatedInventoryItem = inventoryClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(updatedInventoryItem).isEqualTo(testInventoryItem);
    }

    @Test
    @Order(3)
    public void shouldDeactivateInventoryItem() {
        inventoryClient.updateInventoryItemStatus(testInventoryItem.getId(), "DEACTIVATED")
                .then()
                .statusCode(200);

        InventoryItemDTO item = inventoryClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(item.getStatus()).isEqualTo("DEACTIVATED");
    }


    @Test
    public void shouldGatherInventoryItemWithEnoughStock() {
        int newStock = 3;
        int gatherQuantity = 2;
        int expectedStock = 1;

        testInventoryItem.setStock(newStock);
        inventoryClient.updateInventoryItem(testInventoryItem);

        Response response = inventoryClient.gatherInventoryItem(testInventoryItem.getId(), gatherQuantity);
        response.then()
                .statusCode(200);

        testInventoryItem = inventoryClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(testInventoryItem.getStock()).isEqualTo(expectedStock);
    }

    @Test
    public void shouldGatherNegativeQuantityInventoryItem() {
        Response response = inventoryClient.gatherInventoryItem(testInventoryItem.getId(), -1);
        response.then()
                .statusCode(400);
    }

    @Test
    public void shouldGatherInventoryItemWithNotEnoughStock() {
        int newStock = 1;
        int gatherQuantity = 2;

        testInventoryItem.setStock(newStock);
        inventoryClient.updateInventoryItem(testInventoryItem);

        Response response = inventoryClient.gatherInventoryItem(testInventoryItem.getId(), gatherQuantity);
        ApiErrorDTO apiErrorDTO = response.then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("NOT_ENOUGH_STOCK");

    }

    @Test
    public void shouldGatherCompleteStockOfInventoryItem() {
        int gatherQuantity = 5;

        Response response = inventoryClient.gatherInventoryItem(testInventoryItem.getId(), gatherQuantity);
        response.then()
                .statusCode(200);

        testInventoryItem = inventoryClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(testInventoryItem.getStock()).isEqualTo(0);
        assertThat(testInventoryItem.getStatus()).isEqualTo("OUT_OF_STOCK");

    }

    @Test
    public void shouldGatherOutOfStockInventoryItem() {
        int newStock = 0;
        int gatherQuantity = 2;

        testInventoryItem.setStock(newStock);
        testInventoryItem.setStatus("OUT_OF_STOCK");
        inventoryClient.updateInventoryItem(testInventoryItem);

        Response response = inventoryClient.gatherInventoryItem(testInventoryItem.getId(), gatherQuantity);
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
        inventoryClient.updateInventoryItem(testInventoryItem);

        Response response = inventoryClient.gatherInventoryItem(testInventoryItem.getId(), gatherQuantity);
        ApiErrorDTO apiErrorDTO = response.then()
                .statusCode(400)
                .extract()
                .as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ITEM_NOT_ORDERABLE");

    }

    @Test
    public void shouldStockUpInventoryItem() {
        Response response = inventoryClient.stockUpInventoryItem(testInventoryItem.getId(), 10);
        response.then()
                .statusCode(200);

        testInventoryItem = inventoryClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(testInventoryItem.getStock()).isEqualTo(15);
        assertThat(testInventoryItem.getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    public void shouldStockUpInventoryItemOutOfStock() {
        int newStock = 0;
        int stockUpQuantity = 2;

        testInventoryItem.setStock(newStock);
        testInventoryItem.setStatus("OUT_OF_STOCK");
        inventoryClient.updateInventoryItem(testInventoryItem);

        testInventoryItem = inventoryClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(testInventoryItem.getStock()).isEqualTo(0);
        assertThat(testInventoryItem.getStatus()).isEqualTo("OUT_OF_STOCK");

        Response response = inventoryClient.stockUpInventoryItem(testInventoryItem.getId(), stockUpQuantity);
        response.then()
                .statusCode(200);

        InventoryItemDTO item = inventoryClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(item.getStock()).isEqualTo(2);
        assertThat(item.getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    public void shouldStockUpDeactivatedInventoryItem() {
        testInventoryItem.setStatus("DEACTIVATED");
        inventoryClient.updateInventoryItem(testInventoryItem);

        int stockUpQuantity = 3;
        int expectedStock = 8;

        Response response = inventoryClient.stockUpInventoryItem(testInventoryItem.getId(), stockUpQuantity);
        response.then()
                .statusCode(200);

        InventoryItemDTO item = inventoryClient.getInventoryItemById(testInventoryItem.getId());
        assertThat(item.getStock()).isEqualTo(expectedStock);
        assertThat(item.getStatus()).isEqualTo("DEACTIVATED");
    }

}
