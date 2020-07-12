package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.UpdateAccountStatusDTO;
import edu.hm.praegla.shoppingcart.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class AccountStatusUpdatedEvent extends Event<UpdateAccountStatusDTO> {

    private UpdateAccountStatusDTO payload;

    public AccountStatusUpdatedEvent(long aggregateId, UpdateAccountStatusDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public UpdateAccountStatusDTO getPayload() {
        return payload;
    }
}
