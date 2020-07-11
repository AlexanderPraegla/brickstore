package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.UpdateAccountStatusDTO;
import lombok.NoArgsConstructor;

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
