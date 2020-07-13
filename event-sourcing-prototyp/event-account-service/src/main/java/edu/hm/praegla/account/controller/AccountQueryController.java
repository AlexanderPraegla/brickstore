package edu.hm.praegla.account.controller;

import edu.hm.praegla.account.entity.Account;
import edu.hm.praegla.account.service.AccountQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "accounts", produces = {"application/json"})
public class AccountQueryController {

    private final AccountQueryService accountQueryService;

    public AccountQueryController(AccountQueryService accountQueryService) {
        this.accountQueryService = accountQueryService;
    }

    @GetMapping("/jwt")
    public String getToken(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getTokenValue();
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
