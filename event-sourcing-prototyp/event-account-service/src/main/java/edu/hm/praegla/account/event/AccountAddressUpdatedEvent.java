package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.UpdateAddressDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AccountAddressUpdatedEvent extends Event<UpdateAddressDTO> {

    private UpdateAddressDTO payload;

    public AccountAddressUpdatedEvent(long aggregateId, UpdateAddressDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public UpdateAddressDTO getPayload() {
        return payload;
    }
}
