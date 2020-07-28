package edu.hm.brickstore.account.event;

import edu.hm.brickstore.account.dto.UpdateAddressDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
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
