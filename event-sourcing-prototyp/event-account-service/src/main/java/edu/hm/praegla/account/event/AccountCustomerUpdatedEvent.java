package edu.hm.praegla.account.event;

import edu.hm.praegla.account.dto.UpdateCustomerDTO;


public class AccountCustomerUpdatedEvent extends Event<UpdateCustomerDTO> {

    public AccountCustomerUpdatedEvent() {
    }

    public AccountCustomerUpdatedEvent(long aggregateId, UpdateCustomerDTO payload) {
        super(aggregateId, payload);
    }
}
