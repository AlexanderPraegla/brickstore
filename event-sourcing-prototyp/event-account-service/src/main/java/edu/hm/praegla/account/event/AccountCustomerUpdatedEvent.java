package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.UpdateCustomerDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AccountCustomerUpdatedEvent extends Event<UpdateCustomerDTO> {

    private UpdateCustomerDTO payload;


    public AccountCustomerUpdatedEvent(long aggregateId, UpdateCustomerDTO payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public UpdateCustomerDTO getPayload() {
        return payload;
    }
}
