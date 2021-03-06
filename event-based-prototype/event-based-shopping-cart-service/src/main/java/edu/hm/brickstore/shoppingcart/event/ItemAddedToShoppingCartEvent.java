package edu.hm.brickstore.shoppingcart.event;

import edu.hm.brickstore.shoppingcart.dto.AddShoppingCartItemDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ItemAddedToShoppingCartEvent extends Event<AddShoppingCartItemDTO> {

    private AddShoppingCartItemDTO payload;

    public ItemAddedToShoppingCartEvent(long aggregateId, AddShoppingCartItemDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public AddShoppingCartItemDTO getPayload() {
        return payload;
    }
}
