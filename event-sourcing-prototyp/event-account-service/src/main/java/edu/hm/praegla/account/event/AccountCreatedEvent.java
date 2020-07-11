package edu.hm.praegla.account.event;

import edu.hm.praegla.account.entity.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountCreatedEvent extends Event<Account> {

    private Account payload;

    public AccountCreatedEvent(long aggregateId, Account payload) {
        super(aggregateId);
        this.payload = payload;
    }

    @Override
    public Account getPayload() {
        return payload;
    }
}
