package edu.hm.praegla.account.event;

import edu.hm.praegla.account.entity.Account;


public class AccountCreatedEvent extends Event<Account> {

    public AccountCreatedEvent() {
    }

    public AccountCreatedEvent(long aggregateId, Account payload) {
        super(aggregateId, payload);
    }
}
