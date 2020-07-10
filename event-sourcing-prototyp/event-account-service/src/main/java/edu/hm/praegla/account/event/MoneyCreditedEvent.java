package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.CreditAccountDTO;

public class MoneyCreditedEvent extends Event<CreditAccountDTO> {

    public MoneyCreditedEvent() {
    }

    public MoneyCreditedEvent(long aggregateId, CreditAccountDTO payload) {
        super(aggregateId, payload);
    }
}
