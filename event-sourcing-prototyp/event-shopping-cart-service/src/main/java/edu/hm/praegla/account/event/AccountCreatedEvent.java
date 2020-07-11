package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.CreateAccountDTO;
import edu.hm.praegla.shoppingcart.event.Event;
import lombok.Data;
import lombok.NoArgsConstructor;

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