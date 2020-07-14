package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.CreditAccountDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
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
