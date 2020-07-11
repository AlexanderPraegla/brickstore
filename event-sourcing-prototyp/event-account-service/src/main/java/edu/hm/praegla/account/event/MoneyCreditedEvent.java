package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.CreditAccountDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MoneyCreditedEvent extends Event<CreditAccountDTO> {

    private CreditAccountDTO payload;

    public MoneyCreditedEvent(long aggregateId, CreditAccountDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public CreditAccountDTO getPayload() {
        return payload;
    }
}
