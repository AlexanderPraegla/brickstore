package edu.hm.brickstore.parameterResolver;

import edu.hm.brickstore.inventory.dto.InventoryItemDTO;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.math.BigDecimal;
import java.util.Random;

public class InventoryItemParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == InventoryItemDTO.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Object ret = null;
        if (parameterContext.getParameter().getType() == InventoryItemDTO.class) {
            ret = VALID_INVENTORY_ITEMS[new Random().nextInt(VALID_INVENTORY_ITEMS.length)];
        }
        return ret;
    }

    public static final InventoryItemDTO[] VALID_INVENTORY_ITEMS = {
            new InventoryItemDTO(2, "Harry Potter - Große Halle", new BigDecimal("299.99"), "AVAILABLE", 5),
            new InventoryItemDTO(1, "Harry Potter - Hagrids Hütte", new BigDecimal("49.99"), "AVAILABLE", 5),
            new InventoryItemDTO(1, "Harry Potter - Peitschende Weide", new BigDecimal("59.99"), "AVAILABLE", 5),
            new InventoryItemDTO(7, "Harry Potter - Fuchsbau", new BigDecimal("99.99"), "AVAILABLE", 5),
            new InventoryItemDTO(7, "Harry Potter - Durmstrangs Schiff", new BigDecimal("199.99"), "AVAILABLE", 5),
            new InventoryItemDTO(2, "Herr der Ringe - Helms Klamm", new BigDecimal("149.99"), "AVAILABLE", 5),
            new InventoryItemDTO(7, "Herr der Ringe - Minas Tirith", new BigDecimal("629.99"), "AVAILABLE", 5),
            new InventoryItemDTO(3, "Herr der Ringe - Isengard", new BigDecimal("129.99"), "AVAILABLE", 5),
            new InventoryItemDTO(6, "Herr der Ringe - Beutelsend", new BigDecimal("49.99"), "AVAILABLE", 5),
            new InventoryItemDTO(6, "Herr der Ringe - Elbenschiff", new BigDecimal("89.99"), "AVAILABLE", 5),
            new InventoryItemDTO(1, "Star Wars - Millennium Falke", new BigDecimal("699.99"), "AVAILABLE", 5),
            new InventoryItemDTO(4, "Star Wars - TIE Fighter", new BigDecimal("99.99"), "AVAILABLE", 5),
            new InventoryItemDTO(2, "Star Wars - X-Wing", new BigDecimal("199.99"), "AVAILABLE", 5),
            new InventoryItemDTO(9, "Star Wars - A-Wing", new BigDecimal("29.99"), "AVAILABLE", 5),
            new InventoryItemDTO(2, "Star Wars - TIE Bomber", new BigDecimal("19.99"), "AVAILABLE", 5),
            new InventoryItemDTO(1, "Star Wars - Sternenzerstörer", new BigDecimal("799.99"), "AVAILABLE", 5),
            new InventoryItemDTO(1, "GoT - Die große Mauer", new BigDecimal("799.99"), "AVAILABLE", 5),
            new InventoryItemDTO(45, "GoT - Königsmund", new BigDecimal("1099.99"), "AVAILABLE", 5),
            new InventoryItemDTO(1, "Bauwerke - Eiffelturm", new BigDecimal("29.99"), "AVAILABLE", 5),
            new InventoryItemDTO(3, "Bauwerke - Notre-Dame de Paris", new BigDecimal("19.99"), "AVAILABLE", 5),
            new InventoryItemDTO(2, "Bauwerke - Brandenburger Tor", new BigDecimal("49.99"), "AVAILABLE", 5),
            new InventoryItemDTO(1, "Bauwerke - London Bridge", new BigDecimal("49.99"), "AVAILABLE", 5),
            new InventoryItemDTO(3, "Bauwerke - Olympiaturm München", new BigDecimal("19.99"), "AVAILABLE", 5),
            new InventoryItemDTO(2, "Bauwerke - Taj Mahal", new BigDecimal("299.99"), "AVAILABLE", 5),
            new InventoryItemDTO(1, "Bauwerke - Berliner Fernsehturm", new BigDecimal("149.99"), "AVAILABLE", 5),
            new InventoryItemDTO(1, "Bauwerke - Allianz Arena", new BigDecimal("249.99"), "AVAILABLE", 5),
            new InventoryItemDTO(1, "Bauwerke - Frauenkirche Dresden", new BigDecimal("17.99"), "AVAILABLE", 5),
    };
}
