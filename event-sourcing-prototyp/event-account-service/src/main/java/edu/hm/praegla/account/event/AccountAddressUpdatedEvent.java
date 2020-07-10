package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.UpdateAddressDTO;


public class AccountAddressUpdatedEvent extends Event<UpdateAddressDTO> {

    public AccountAddressUpdatedEvent() {
    }

    public AccountAddressUpdatedEvent(long aggregateId, UpdateAddressDTO payload) {
        super(aggregateId, payload);
    }
}
