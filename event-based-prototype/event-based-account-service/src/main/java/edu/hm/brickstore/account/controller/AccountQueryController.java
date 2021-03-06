package edu.hm.brickstore.account.controller;

import edu.hm.brickstore.account.entity.Account;
import edu.hm.brickstore.account.service.AccountQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "accounts", produces = {"application/json"})
@Tag(name = "Account query API")
public class AccountQueryController {

    private final AccountQueryService accountQueryService;

    public AccountQueryController(AccountQueryService accountQueryService) {
        this.accountQueryService = accountQueryService;
    }

    @PreAuthorize("hasAuthority('admins')")
    @GetMapping
    public Iterable<Account> getAccounts() {
        return accountQueryService.getAccounts();
    }

    @PreAuthorize("hasAuthority('customers')")
    @GetMapping("/{accountId}")
    public Account getAccount(@PathVariable long accountId) {
        return accountQueryService.getAccount(accountId);
    }

}
