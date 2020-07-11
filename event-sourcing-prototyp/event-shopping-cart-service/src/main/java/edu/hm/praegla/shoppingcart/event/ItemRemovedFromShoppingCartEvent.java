package edu.hm.praegla.shoppingcart.event;

import edu.hm.praegla.shoppingcart.dto.RemoveShoppingCartItemDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

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
