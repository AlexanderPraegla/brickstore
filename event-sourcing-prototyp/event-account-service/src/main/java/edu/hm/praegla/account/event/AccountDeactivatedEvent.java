package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.DeactivateAccountDTO;

public class AccountDeactivatedEvent extends Event<DeactivateAccountDTO> {

    public AccountDeactivatedEvent() {
    }

    public AccountDeactivatedEvent(long aggregateId, DeactivateAccountDTO payload) {
        super(aggregateId, payload);
    }
}
