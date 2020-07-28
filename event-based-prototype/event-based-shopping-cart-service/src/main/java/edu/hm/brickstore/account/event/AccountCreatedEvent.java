package edu.hm.brickstore.account.event;

import edu.hm.brickstore.account.dto.CreateAccountDTO;
import edu.hm.brickstore.shoppingcart.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AccountCreatedEvent extends Event<CreateAccountDTO> {

    private CreateAccountDTO payload;

    public AccountCreatedEvent(long aggregateId, CreateAccountDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public CreateAccountDTO getPayload() {
        return payload;
    }
}
