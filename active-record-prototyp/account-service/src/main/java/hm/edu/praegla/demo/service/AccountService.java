package hm.edu.praegla.demo.service;

import hm.edu.praegla.demo.entity.Account;
import hm.edu.praegla.demo.entity.AccountStatus;
import hm.edu.praegla.demo.entity.Address;
import hm.edu.praegla.demo.entity.Customer;
import hm.edu.praegla.demo.error.BalanceInsufficientException;
import hm.edu.praegla.demo.error.EntityNotFoundException;
import hm.edu.praegla.demo.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
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

    public void debitBalance(long accountId, double amount) {
        Account account = getAccount(accountId);

        double balance = account.getBalance();
        if (balance < amount) {
            throw new BalanceInsufficientException();
        }

        account.setBalance(balance - amount);
    }

    public void chargeBalance(long accountId, double amount) {
        Account account = getAccount(accountId);

        if (account.getStatus() == AccountStatus.CREATED && amount > 0) {
            account.setStatus(AccountStatus.ACTIVE);
        }
        double balance = account.getBalance();
        account.setBalance(balance + amount);
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
