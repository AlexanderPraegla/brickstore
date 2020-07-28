package edu.hm.brickstore.account.service;

import edu.hm.brickstore.account.entity.Account;
import edu.hm.brickstore.account.repository.AccountRepository;
import edu.hm.brickstore.error.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class AccountQueryService {

    private final AccountRepository accountRepository;

    public AccountQueryService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getAccount(long accountId) {
        log.info("Get account for accountId={}", accountId);
        return accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException(Account.class, "id", accountId));
    }

    public Iterable<Account> getAccounts() {
        log.info("Get all accounts");
        return accountRepository.findAll();
    }

}
