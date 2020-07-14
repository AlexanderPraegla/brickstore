package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.DebitAccountDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MoneyDebitedEvent extends Event<DebitAccountDTO> {

    private DebitAccountDTO payload;

    public MoneyDebitedEvent(long aggregateId, DebitAccountDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public DebitAccountDTO getPayload() {
        return payload;
    }
}
