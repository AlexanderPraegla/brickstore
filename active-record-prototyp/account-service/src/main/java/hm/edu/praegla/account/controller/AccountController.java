package hm.edu.praegla.account.controller;

import hm.edu.praegla.account.entity.Account;
import hm.edu.praegla.account.entity.AccountStatus;
import hm.edu.praegla.account.entity.Address;
import hm.edu.praegla.account.entity.Customer;
import hm.edu.praegla.account.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(value = "accounts", produces = {"application/json"})
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/jwt")
    public String getToken(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getTokenValue();
    }

    @GetMapping
    public Iterable<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @GetMapping("/{accountId}")
    public Account getAccount(@PathVariable long accountId) {
        return accountService.getAccount(accountId);
    }

    @PutMapping
    public ResponseEntity<?> createAccount(UriComponentsBuilder b, @Valid @RequestBody CreateAccountDTO createAccountDTO) {
        Account account = accountService.createAccount(createAccountDTO.customer, createAccountDTO.address);

        UriComponents uriComponents = b.path("/account/{accountId}").buildAndExpand(account.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @GetMapping("/{accountId}/customer")
    public ResponseEntity<Customer> getAccountCustomer(@PathVariable long accountId) {
        Customer accountCustomer = accountService.getAccountCustomer(accountId);
        return ResponseEntity.ok(accountCustomer);
    }

    @GetMapping("/{accountId}/address")
    public ResponseEntity<Address> getAccountAddress(@PathVariable long accountId) {
        Address accountAddress = accountService.getAccountAddress(accountId);
        return ResponseEntity.ok(accountAddress);
    }

    @PostMapping("/{accountId}/customer")
    public ResponseEntity<?> updateCustomer(@PathVariable long accountId, @Valid @RequestBody ModifyCustomerDTO modifyCustomerDTO) {
        accountService.updateCustomer(accountId, modifyCustomerDTO.firstname, modifyCustomerDTO.lastname, modifyCustomerDTO.email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/address")
    public ResponseEntity<?> updateAddress(@PathVariable long accountId, @Valid @RequestBody ModifyAddressDTO modifyAddressDTO) {
        accountService.updateAddress(accountId, modifyAddressDTO.street, modifyAddressDTO.city, modifyAddressDTO.postalCode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/debit")
    public ResponseEntity<?> debitBalance(@PathVariable long accountId, @Valid @RequestBody ModifyAccountBalanceDTO chargeAccountBalanceDTO) {
        accountService.debitBalance(accountId, chargeAccountBalanceDTO.amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/charge")
    public ResponseEntity<?> chargeBalance(@PathVariable long accountId, @Valid @RequestBody ModifyAccountBalanceDTO chargeAccountBalanceDTO) {
        accountService.chargeBalance(accountId, chargeAccountBalanceDTO.amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable long accountId, @Valid @RequestBody UpdateAccountStatusDTO updateAccountStatusDTO) {
        accountService.updateStatus(accountId, updateAccountStatusDTO.status);
        return ResponseEntity.ok().build();
    }

    private static class ModifyAccountBalanceDTO {
        @Min(1)
        public double amount;
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
