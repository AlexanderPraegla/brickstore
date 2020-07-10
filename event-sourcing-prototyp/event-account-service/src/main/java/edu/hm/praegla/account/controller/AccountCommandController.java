package edu.hm.praegla.account.controller;

import edu.hm.praegla.account.dto.CreditAccountDTO;
import edu.hm.praegla.account.dto.DebitAccountDTO;
import edu.hm.praegla.account.dto.UpdateAddressDTO;
import edu.hm.praegla.account.dto.UpdateCustomerDTO;
import edu.hm.praegla.account.entity.Account;
import edu.hm.praegla.account.entity.AccountStatus;
import edu.hm.praegla.account.service.AccountCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "accounts", produces = {"application/json"})
public class AccountCommandController {

    private final AccountCommandService accountCommandService;

    public AccountCommandController(AccountCommandService accountCommandService) {
        this.accountCommandService = accountCommandService;
    }

    @PutMapping
    public ResponseEntity<?> createAccount(UriComponentsBuilder b, @Valid @RequestBody Account account) {
        Account createdAccount = accountCommandService.createAccountCommand(account);
        UriComponents uriComponents = b.path("/accounts/{accountId}").buildAndExpand(createdAccount.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PostMapping("/{accountId}/customer")
    public ResponseEntity<?> updateCustomer(@PathVariable long accountId, @Valid @RequestBody UpdateCustomerDTO updateCustomerDTO) {
        accountCommandService.updateCustomer(accountId, updateCustomerDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/address")
    public ResponseEntity<?> updateAddress(@PathVariable long accountId, @Valid @RequestBody UpdateAddressDTO updateAddressDTO) {
        accountCommandService.updateAddress(accountId, updateAddressDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/debit")
    public ResponseEntity<?> debitBalance(@PathVariable long accountId, @Valid @RequestBody DebitAccountDTO debitAccountDTO) {
        accountCommandService.debitMoneyFromAccount(accountId, debitAccountDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/credit")
    public ResponseEntity<?> creditBalance(@PathVariable long accountId, @Valid @RequestBody CreditAccountDTO creditAccountDTO) {
        accountCommandService.creditMoneyToAccount(accountId, creditAccountDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable long accountId, @Valid @RequestBody UpdateAccountStatusDTO updateAccountStatusDTO) {
        accountCommandService.updateStatus(accountId, updateAccountStatusDTO.status);
        return ResponseEntity.ok().build();
    }

    private static class UpdateAccountStatusDTO {
        @NotNull
        public AccountStatus status;
    }

}
