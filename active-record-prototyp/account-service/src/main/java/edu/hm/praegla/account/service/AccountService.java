package edu.hm.praegla.account.service;

import edu.hm.praegla.account.entity.Account;
import edu.hm.praegla.account.entity.AccountStatus;
import edu.hm.praegla.account.entity.Address;
import edu.hm.praegla.account.entity.Customer;
import edu.hm.praegla.account.error.AccountDeactivatedException;
import edu.hm.praegla.account.error.BalanceInsufficientException;
import edu.hm.praegla.account.error.EntityNotFoundException;
import edu.hm.praegla.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getAccount(long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException(Account.class, "id", accountId));
    }

    public Iterable<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Account createAccount(Customer customer, Address address) {
        Account account = new Account();
        account.setCustomer(customer);
        account.setAddress(address);
        account.setStatus(AccountStatus.CREATED);

        return accountRepository.save(account);
    }

    public void debitBalance(long accountId, BigDecimal amount) {
        Account account = getAccount(accountId);

        if (account.getStatus() == AccountStatus.DEACTIVATED) {
            throw new AccountDeactivatedException();
        }

        BigDecimal balance = account.getBalance();
        if (balance.compareTo(amount) < 0) {
            throw new BalanceInsufficientException();
        }

        account.setBalance(balance.subtract(amount));
    }

    public void creditBalance(long accountId, BigDecimal amount) {
        Account account = getAccount(accountId);

        if (account.getStatus() == AccountStatus.DEACTIVATED) {
            throw new AccountDeactivatedException();
        }

        if (account.getStatus() == AccountStatus.CREATED && amount.compareTo(BigDecimal.ZERO) > 0) {
            account.setStatus(AccountStatus.ACTIVATED);
        }
        BigDecimal balance = account.getBalance();
        account.setBalance(balance.add(amount));
    }

    public void updateStatus(long accountId, AccountStatus status) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        Account account = optionalAccount.orElseThrow(() -> new EntityNotFoundException(Account.class, "id", accountId));
        account.setStatus(status);
    }

    public void updateCustomer(long accountId, String firstname, String lastname, String email) {
        Account account = getAccount(accountId);
        @NotNull Customer customer = account.getCustomer();
        customer.setFirstname(firstname);
        customer.setLastname(lastname);
        customer.setEmail(email);
    }

    public void updateAddress(long accountId, String street, String city, String postalCode) {
        Account account = getAccount(accountId);
        @NotNull Address address = account.getAddress();
        address.setStreet(street);
        address.setCity(city);
        address.setPostalCode(postalCode);
    }

    public Address getAccountAddress(long accountId) {
        return getAccount(accountId).getAddress();
    }

    public Customer getAccountCustomer(long accountId) {
        return getAccount(accountId).getCustomer();
    }
}