package edu.hm.praegla.account.service;

import edu.hm.praegla.account.dto.CreateAccountDTO;
import edu.hm.praegla.account.dto.CreditAccountDTO;
import edu.hm.praegla.account.dto.DebitAccountDTO;
import edu.hm.praegla.account.dto.UpdateAccountStatusDTO;
import edu.hm.praegla.account.dto.UpdateAddressDTO;
import edu.hm.praegla.account.dto.UpdateCustomerDTO;
import edu.hm.praegla.account.entity.Account;
import edu.hm.praegla.account.entity.AccountStatus;
import edu.hm.praegla.account.error.AccountDeactivatedException;
import edu.hm.praegla.account.error.BalanceInsufficientException;
import edu.hm.praegla.account.event.AccountAddressUpdatedEvent;
import edu.hm.praegla.account.event.AccountCreatedEvent;
import edu.hm.praegla.account.event.AccountCustomerUpdatedEvent;
import edu.hm.praegla.account.event.AccountStatusUpdatedEvent;
import edu.hm.praegla.account.event.Event;
import edu.hm.praegla.account.event.MoneyCreditedEvent;
import edu.hm.praegla.account.event.MoneyDebitedEvent;
import edu.hm.praegla.account.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.math.BigDecimal;

@Service
@Transactional
public class AccountCommandService {

    private final SequenceGeneratorService sequenceGenerator;
    private final AccountQueryService accountQueryService;
    private final EventRepository eventRepository;
    private final AccountMessagingService accountMessagingService;

    public AccountCommandService(SequenceGeneratorService sequenceGenerator, AccountQueryService accountQueryService, EventRepository eventRepository, AccountMessagingService accountMessagingService) {
        this.sequenceGenerator = sequenceGenerator;
        this.accountQueryService = accountQueryService;
        this.eventRepository = eventRepository;
        this.accountMessagingService = accountMessagingService;
    }

    public CreateAccountDTO createAccountCommand(CreateAccountDTO createAccountDTO) {
        long accountId = sequenceGenerator.generateSequence(Account.SEQUENCE_NAME);
        createAccountDTO.setId(accountId);

        AccountCreatedEvent event = new AccountCreatedEvent(createAccountDTO.getId(), createAccountDTO);
        eventRepository.save(event);

        accountMessagingService.sendMessage(event);

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
        accountMessagingService.sendMessage(event);
        ;
    }

    public void creditMoneyToAccount(long accountId, CreditAccountDTO creditAccountDTO) {
        Account account = accountQueryService.getAccount(accountId);
        if (account.getStatus() == AccountStatus.DEACTIVATED) {
            throw new AccountDeactivatedException();
        }
        MoneyCreditedEvent event = new MoneyCreditedEvent(accountId, creditAccountDTO);
        eventRepository.save(event);
        accountMessagingService.sendMessage(event);
        ;
    }

    public void updateStatus(long accountId, @Valid UpdateAccountStatusDTO updateAccountStatusDTO) {
        Account account = accountQueryService.getAccount(accountId);
        Event<UpdateAccountStatusDTO> event = new AccountStatusUpdatedEvent(accountId, updateAccountStatusDTO);
        eventRepository.save(event);
        accountMessagingService.sendMessage(event);
    }

    public void updateCustomer(long accountId, UpdateCustomerDTO updateCustomerDTO) {
        Account account = accountQueryService.getAccount(accountId);
        AccountCustomerUpdatedEvent event = new AccountCustomerUpdatedEvent(accountId, updateCustomerDTO);
        eventRepository.save(event);
        accountMessagingService.sendMessage(event);
        ;
    }

    public void updateAddress(long accountId, UpdateAddressDTO updateAddressDTO) {
        Account account = accountQueryService.getAccount(accountId);
        AccountAddressUpdatedEvent event = new AccountAddressUpdatedEvent(accountId, updateAddressDTO);
        eventRepository.save(event);
        accountMessagingService.sendMessage(event);
        ;
    }
}