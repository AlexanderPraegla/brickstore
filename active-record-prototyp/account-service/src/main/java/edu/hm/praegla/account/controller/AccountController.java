package edu.hm.praegla.account.controller;

import edu.hm.praegla.account.entity.Account;
import edu.hm.praegla.account.entity.AccountStatus;
import edu.hm.praegla.account.entity.Address;
import edu.hm.praegla.account.entity.Customer;
import edu.hm.praegla.account.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Validated
@RestController
@RequestMapping(value = "accounts", produces = {"application/json"})
@Tag(name = "Account API")
public class AccountController {

    @Value("${spring.gateway.host}")
    private String host;
    @Value("${spring.gateway.port}")
    private String port;
    @Value("${spring.gateway.scheme}")
    private String scheme;

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("hasAuthority('admins')")
    @GetMapping
    public Iterable<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @PreAuthorize("hasAuthority('customers')")
    @GetMapping("/{accountId}")
    public Account getAccount(@PathVariable long accountId) {
        return accountService.getAccount(accountId);
    }

    @PreAuthorize("hasAuthority('admins')")
    @PutMapping
    public ResponseEntity<?> createAccount(UriComponentsBuilder b, @Valid @RequestBody CreateAccountDTO createAccountDTO) {
        Account account = accountService.createAccount(createAccountDTO.customer, createAccountDTO.address);

        UriComponents uriComponents = b.scheme(scheme).host(host).port(port).path("/accounts/{accountId}").buildAndExpand(account.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PreAuthorize("hasAuthority('customers')")
    @GetMapping("/{accountId}/customer")
    public ResponseEntity<Customer> getAccountCustomer(@PathVariable long accountId) {
        Customer accountCustomer = accountService.getAccountCustomer(accountId);
        return ResponseEntity.ok(accountCustomer);
    }

    @PreAuthorize("hasAuthority('customers')")
    @GetMapping("/{accountId}/address")
    public ResponseEntity<Address> getAccountAddress(@PathVariable long accountId) {
        Address accountAddress = accountService.getAccountAddress(accountId);
        return ResponseEntity.ok(accountAddress);
    }

    @PreAuthorize("hasAuthority('customers')")
    @PostMapping("/{accountId}/customer")
    public ResponseEntity<?> updateCustomer(@PathVariable long accountId, @Valid @RequestBody ModifyCustomerDTO modifyCustomerDTO) {
        accountService.updateCustomer(accountId, modifyCustomerDTO.firstname, modifyCustomerDTO.lastname, modifyCustomerDTO.email);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('customers')")
    @PostMapping("/{accountId}/address")
    public ResponseEntity<?> updateAddress(@PathVariable long accountId, @Valid @RequestBody ModifyAddressDTO modifyAddressDTO) {
        accountService.updateAddress(accountId, modifyAddressDTO.street, modifyAddressDTO.city, modifyAddressDTO.postalCode);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('customers')")
    @PostMapping("/{accountId}/debit")
    public ResponseEntity<?> debitBalance(@PathVariable long accountId, @Valid @RequestBody DebitAccountDTO debitAccountDTO) {
        accountService.debitBalance(accountId, debitAccountDTO.debitAmount);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('customers')")
    @PostMapping("/{accountId}/credit")
    public ResponseEntity<?> creditBalance(@PathVariable long accountId, @Valid @RequestBody CreditAccountDTO creditAccountDTO) {
        accountService.creditBalance(accountId, creditAccountDTO.creditAmount);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('customers')")
    @PostMapping("/{accountId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable long accountId, @Valid @RequestBody UpdateAccountStatusDTO updateAccountStatusDTO) {
        accountService.updateStatus(accountId, updateAccountStatusDTO.status);
        return ResponseEntity.ok().build();
    }

    private static class DebitAccountDTO {
        @DecimalMin(value = "1")
        @Column(precision = 7, scale = 2)
        public BigDecimal debitAmount;
    }

    private static class CreditAccountDTO {
        @DecimalMin(value = "1")
        @Column(precision = 7, scale = 2)
        public BigDecimal creditAmount;
    }

    private static class UpdateAccountStatusDTO {
        @NotNull
        public AccountStatus status;
    }

    private static class CreateAccountDTO {
        @NotNull
        public Customer customer;
        @NotNull
        public Address address;
    }

    private static class ModifyCustomerDTO {
        @NotNull
        public String firstname;
        @NotNull
        public String lastname;
        @NotNull
        public String email;
    }

    private static class ModifyAddressDTO {
        @NotNull
        public String street;
        @NotNull
        public String city;
        @NotNull
        public String postalCode;
    }

}
