package edu.hm.praegla.account.service;

import edu.hm.praegla.account.config.MessagingRabbitMqConfig;
import edu.hm.praegla.account.dto.ActivateAccountDTO;
import edu.hm.praegla.account.dto.CreditAccountDTO;
import edu.hm.praegla.account.dto.DeactivateAccountDTO;
import edu.hm.praegla.account.dto.DebitAccountDTO;
import edu.hm.praegla.account.dto.UpdateAddressDTO;
import edu.hm.praegla.account.dto.UpdateCustomerDTO;
import edu.hm.praegla.account.entity.Account;
import edu.hm.praegla.account.entity.AccountStatus;
import edu.hm.praegla.account.event.Event;
import edu.hm.praegla.account.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@Transactional
public class MessageListenerService {

    private final AccountRepository accountRepository;
    private final AccountQueryService accountQueryService;

    public MessageListenerService(AccountRepository accountRepository, AccountQueryService accountQueryService) {
        this.accountRepository = accountRepository;
        this.accountQueryService = accountQueryService;
    }

    @RabbitListener(queues = MessagingRabbitMqConfig.ACCOUNT_SERVICE_QUEUE)
    public void listenAccountCreatedEvent(@Payload Event<?> event, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key) {
        log.info("Event received: {}", event);
        switch (event.getEventType()) {
            case "AccountCreatedEvent":
                handleAccountCreatedEvent((Event<Account>) event);
                break;
            case "AccountActivatedEvent":
                handleAccountActivatedEvent((Event<ActivateAccountDTO>) event);
                break;
            case "AccountDeactivatedEvent":
                handleAccountDeactivatedEvent((Event<DeactivateAccountDTO>) event);
                break;
            case "MoneyCreditedEvent":
                handleMoneyCreditedEvent((Event<CreditAccountDTO>) event);
                break;
            case "MoneyDebitedEvent":
                handleMoneyDebitedEvent((Event<DebitAccountDTO>) event);
                break;
            case "AccountCustomerUpdatedEvent":
                handleAccountCustomerUpdatedEvent((Event<UpdateCustomerDTO>) event);
                break;
            case "AccountAddressUpdatedEvent":
                handleAccountAddressUpdatedEvent((Event<UpdateAddressDTO>) event);
                break;
            default:
                log.error("There is no handler for the event type '{}}'", event.getEventType());
        }
    }

    public void handleAccountCreatedEvent(Event<Account> event) {
        accountRepository.save(event.getPayload());
    }

    public void handleAccountActivatedEvent(Event<ActivateAccountDTO> event) {

        Account account = accountQueryService.getAccount(event.getAggregateId());
        account.setStatus(AccountStatus.ACTIVATED);
        accountRepository.save(account);
    }

    public void handleAccountCustomerUpdatedEvent(Event<UpdateCustomerDTO> event) {
        Account account = accountQueryService.getAccount(event.getAggregateId());
        Account.Customer customer = account.getCustomer();
        UpdateCustomerDTO payload = event.getPayload();
        customer.setFirstname(payload.getFirstname());
        customer.setLastname(payload.getLastname());
        customer.setEmail(payload.getEmail());
        account.setCustomer(customer);
        accountRepository.save(account);
    }

    public void handleAccountAddressUpdatedEvent(Event<UpdateAddressDTO> event) {
        Account account = accountQueryService.getAccount(event.getAggregateId());
        Account.Address address = account.getAddress();
        UpdateAddressDTO payload = event.getPayload();
        address.setCity(payload.getCity());
        address.setStreet(payload.getStreet());
        address.setPostalCode(payload.getPostalCode());
        account.setAddress(address);
        accountRepository.save(account);
    }

    public void handleAccountDeactivatedEvent(Event<DeactivateAccountDTO> event) {
        Account account = accountQueryService.getAccount(event.getAggregateId());
        account.setStatus(AccountStatus.DEACTIVATED);
        accountRepository.save(account);
    }

    public void handleMoneyCreditedEvent(Event<CreditAccountDTO> event) {
        Account account = accountQueryService.getAccount(event.getAggregateId());
        BigDecimal balance = account.getBalance();
        account.setBalance(balance.add(event.getPayload().getCreditAmount()));

        if (account.getStatus() == AccountStatus.CREATED) {
            account.setStatus(AccountStatus.ACTIVATED);
        }
        accountRepository.save(account);
    }

    public void handleMoneyDebitedEvent(Event<DebitAccountDTO> event) {
        Account account = accountQueryService.getAccount(event.getAggregateId());
        BigDecimal balance = account.getBalance();
        account.setBalance(balance.subtract(event.getPayload().getDebitAmount()));
        accountRepository.save(account);
    }
}
