package edu.hm.brickstore.shoppingcart.event;

import edu.hm.brickstore.shoppingcart.dto.RemoveShoppingCartItemDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ItemRemovedFromShoppingCartEvent extends Event<RemoveShoppingCartItemDTO> {

    private RemoveShoppingCartItemDTO payload;

    public ItemRemovedFromShoppingCartEvent(long aggregateId, RemoveShoppingCartItemDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public RemoveShoppingCartItemDTO getPayload() {
        return payload;
    }
}
