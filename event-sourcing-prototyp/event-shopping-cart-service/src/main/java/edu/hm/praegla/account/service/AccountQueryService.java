package edu.hm.praegla.account.service;

import edu.hm.praegla.account.entity.Account;
import edu.hm.praegla.account.repository.AccountRepository;
import edu.hm.praegla.error.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountQueryService {

    private final AccountRepository accountRepository;

    public AccountQueryService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public Account getAccount(long accountId) {
        return accountRepository.findByAccountId(accountId).orElseThrow(() -> new EntityNotFoundException(Account.class, "id", accountId));
    }

}
