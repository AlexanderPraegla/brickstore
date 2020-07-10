package edu.hm.praegla.account.service;

import edu.hm.praegla.account.dto.ActivateAccountDTO;
import edu.hm.praegla.account.dto.CreditAccountDTO;
import edu.hm.praegla.account.dto.DeactivateAccountDTO;
import edu.hm.praegla.account.dto.DebitAccountDTO;
import edu.hm.praegla.account.dto.UpdateAddressDTO;
import edu.hm.praegla.account.dto.UpdateCustomerDTO;
import edu.hm.praegla.account.entity.Account;
import edu.hm.praegla.account.entity.AccountStatus;
import edu.hm.praegla.account.error.AccountDeactivatedException;
import edu.hm.praegla.account.error.BalanceInsufficientException;
import edu.hm.praegla.account.event.AccountActivatedEvent;
import edu.hm.praegla.account.event.AccountAddressUpdatedEvent;
import edu.hm.praegla.account.event.AccountCreatedEvent;
import edu.hm.praegla.account.event.AccountCustomerUpdatedEvent;
import edu.hm.praegla.account.event.AccountDeactivatedEvent;
import edu.hm.praegla.account.event.Event;
import edu.hm.praegla.account.event.MoneyCreditedEvent;
import edu.hm.praegla.account.event.MoneyDebitedEvent;
import edu.hm.praegla.account.repository.EventRepository;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class AccountCommandService {

    private final SequenceGeneratorService sequenceGenerator;
    private final AccountQueryService accountQueryService;
    private final EventRepository eventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Exchange exchange;

    public AccountCommandService(SequenceGeneratorService sequenceGenerator, AccountQueryService accountQueryService, EventRepository eventRepository, RabbitTemplate rabbitTemplate, Exchange exchange) {
        this.sequenceGenerator = sequenceGenerator;
        this.accountQueryService = accountQueryService;
        this.eventRepository = eventRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public Account createAccountCommand(Account createAccountDTO) {
        long accountId = sequenceGenerator.generateSequence(Account.SEQUENCE_NAME);
        createAccountDTO.setId(accountId);
        createAccountDTO.setBalance(new BigDecimal("0.00"));
        createAccountDTO.setStatus(AccountStatus.CREATED);

        AccountCreatedEvent event = new AccountCreatedEvent(createAccountDTO.getId(), createAccountDTO);
        eventRepository.save(event);

        rabbitTemplate.convertAndSend(exchange.getName(), "account." + event.getEventType(), event);

        return createAccountDTO;
    }

    public void debitMoneyFromAccount(long accountId, DebitAccountDTO debitAccountDTO) {
        Account account = accountQueryService.getAccount(accountId);

        if (account.getStatus() == AccountStatus.DEACTIVATED) {
            throw new AccountDeactivatedException();
        }

        BigDecimal balance = account.getBalance();
        if (balance.compareTo(debitAccountDTO.getDebitAmount()) < 0) {
            throw new BalanceInsufficientException();
        }

        MoneyDebitedEvent event = new MoneyDebitedEvent(accountId, debitAccountDTO);
        eventRepository.save(event);
        rabbitTemplate.convertAndSend(exchange.getName(), "account." + event.getEventType(), event);
    }

    public void creditMoneyToAccount(long accountId, CreditAccountDTO creditAccountDTO) {
        Account account = accountQueryService.getAccount(accountId);
        if (account.getStatus() == AccountStatus.DEACTIVATED) {
            throw new AccountDeactivatedException();
        }
        MoneyCreditedEvent event = new MoneyCreditedEvent(accountId, creditAccountDTO);
        eventRepository.save(event);
        rabbitTemplate.convertAndSend(exchange.getName(), "account." + event.getEventType(), event);
    }

    public void updateStatus(long accountId, AccountStatus status) {
        Account account = accountQueryService.getAccount(accountId);
        Event<?> event = null;
        switch (status) {
            case ACTIVATED:
                event = new AccountActivatedEvent(accountId, new ActivateAccountDTO());
                break;
            case DEACTIVATED:
                event = new AccountDeactivatedEvent(accountId, new DeactivateAccountDTO());
                break;
        }

        if (event != null) {
            eventRepository.save(event);
            rabbitTemplate.convertAndSend(exchange.getName(), "account." + event.getEventType(), event);
        }
    }

    public void updateCustomer(long accountId, UpdateCustomerDTO updateCustomerDTO) {
        Account account = accountQueryService.getAccount(accountId);
        AccountCustomerUpdatedEvent event = new AccountCustomerUpdatedEvent(accountId, updateCustomerDTO);
        eventRepository.save(event);
        rabbitTemplate.convertAndSend(exchange.getName(), "account." + event.getEventType(), event);
    }

    public void updateAddress(long accountId, UpdateAddressDTO updateAddressDTO) {
        Account account = accountQueryService.getAccount(accountId);
        AccountAddressUpdatedEvent event = new AccountAddressUpdatedEvent(accountId, updateAddressDTO);
        eventRepository.save(event);
        rabbitTemplate.convertAndSend(exchange.getName(), "account." + event.getEventType(), event);
    }
}
