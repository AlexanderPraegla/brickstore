package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.DebitAccountDTO;

public class MoneyDebitedEvent extends Event<DebitAccountDTO> {

    public MoneyDebitedEvent() {
    }

    public MoneyDebitedEvent(long aggregateId, DebitAccountDTO payload) {
        super(aggregateId, payload);
    }
}
