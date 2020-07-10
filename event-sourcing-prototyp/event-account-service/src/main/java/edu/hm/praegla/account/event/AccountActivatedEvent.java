package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.ActivateAccountDTO;

public class AccountActivatedEvent extends Event<ActivateAccountDTO> {

    public AccountActivatedEvent() {
    }

    public AccountActivatedEvent(long aggregateId, ActivateAccountDTO payload) {
        super(aggregateId, payload);
    }
}
