package edu.hm.brickstore.account.eventhandler;

import edu.hm.brickstore.account.dto.CreateAccountDTO;
import edu.hm.brickstore.account.dto.UpdateAddressDTO;
import edu.hm.brickstore.account.dto.UpdateCustomerDTO;
import edu.hm.brickstore.account.entity.Account;
import edu.hm.brickstore.account.entity.AccountStatus;
import edu.hm.brickstore.account.event.AccountAddressUpdatedEvent;
import edu.hm.brickstore.account.event.AccountCreatedEvent;
import edu.hm.brickstore.account.event.AccountCustomerUpdatedEvent;
import edu.hm.brickstore.account.event.AccountStatusUpdatedEvent;
import edu.hm.brickstore.account.event.MoneyCreditedEvent;
import edu.hm.brickstore.account.event.MoneyDebitedEvent;
import edu.hm.brickstore.account.repository.AccountRepository;
import edu.hm.brickstore.account.service.AccountQueryService;
import edu.hm.brickstore.messaging.config.MessagingRabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Slf4j
@Component
@RabbitListener(queues = MessagingRabbitMqConfig.ACCOUNT_QUEUE)
public class AccountEventHandler {

    private final AccountRepository accountRepository;
    private final AccountQueryService accountQueryService;

    public AccountEventHandler(AccountRepository accountRepository, AccountQueryService accountQueryService) {
        this.accountRepository = accountRepository;
        this.accountQueryService = accountQueryService;
    }

    @RabbitHandler
    public void processAccountCreatedEvent(@Payload AccountCreatedEvent event) {
        log.info("Received AccountCreatedEvent: {}", event);
        CreateAccountDTO payload = event.getPayload();
        Account account = new Account();
        account.setId(payload.getId());
        account.setBalance(new BigDecimal("0.00"));
        account.setStatus(AccountStatus.CREATED);

        CreateAccountDTO.@NotNull AddressDTO addressDTO = payload.getAddress();
        Account.Address address = new Account.Address();
        address.setCity(addressDTO.getCity());
        address.setStreet(addressDTO.getStreet());
        address.setPostalCode(addressDTO.getPostalCode());
        account.setAddress(address);

        CreateAccountDTO.@NotNull CustomerDTO customerDTO = payload.getCustomer();
        Account.Customer customer = new Account.Customer();
        customer.setFirstname(customerDTO.getFirstname());
        customer.setLastname(customerDTO.getLastname());
        customer.setEmail(customerDTO.getEmail());
        account.setCustomer(customer);

        accountRepository.save(account);
    }

    @RabbitHandler
    public void processAccountStatusUpdatedEvent(@Payload AccountStatusUpdatedEvent event) {
        log.info("Received AccountStatusUpdatedEvent: {}", event);
        Account account = accountQueryService.getAccount(event.getAggregateId());
        account.setStatus(event.getPayload().getStatus());
        accountRepository.save(account);
    }

    @RabbitHandler
    public void processAccountCustomerUpdatedEvent(@Payload AccountCustomerUpdatedEvent event) {
        log.info("Received AccountCustomerUpdatedEvent: {}", event);
        Account account = accountQueryService.getAccount(event.getAggregateId());
        Account.Customer customer = account.getCustomer();
        UpdateCustomerDTO payload = event.getPayload();
        customer.setFirstname(payload.getFirstname());
        customer.setLastname(payload.getLastname());
        customer.setEmail(payload.getEmail());
        account.setCustomer(customer);
        accountRepository.save(account);
    }

    @RabbitHandler
    public void processAccountAddressUpdatedEvent(@Payload AccountAddressUpdatedEvent event) {
        log.info("Received AccountAddressUpdatedEvent: {}", event);
        Account account = accountQueryService.getAccount(event.getAggregateId());
        Account.Address address = account.getAddress();
        UpdateAddressDTO payload = event.getPayload();
        address.setCity(payload.getCity());
        address.setStreet(payload.getStreet());
        address.setPostalCode(payload.getPostalCode());
        account.setAddress(address);
        accountRepository.save(account);
    }

    @RabbitHandler
    public void processMoneyCreditedEvent(@Payload MoneyCreditedEvent event) {
        log.info("Received MoneyCreditedEvent: {}", event);
        Account account = accountQueryService.getAccount(event.getAggregateId());
        BigDecimal balance = account.getBalance();
        account.setBalance(balance.add(event.getPayload().getCreditAmount()));

        if (account.getStatus() == AccountStatus.CREATED) {
            account.setStatus(AccountStatus.ACTIVATED);
        }
        accountRepository.save(account);
    }

    @RabbitHandler
    public void processMoneyDebitedEvent(@Payload MoneyDebitedEvent event) {
        log.info("Received MoneyDebitedEvent: {}", event);
        Account account = accountQueryService.getAccount(event.getAggregateId());
        BigDecimal balance = account.getBalance();
        account.setBalance(balance.subtract(event.getPayload().getDebitAmount()));
        accountRepository.save(account);
    }
}
