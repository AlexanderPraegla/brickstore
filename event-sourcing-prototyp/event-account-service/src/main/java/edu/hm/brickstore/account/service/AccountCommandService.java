package edu.hm.brickstore.account.service;

import edu.hm.brickstore.account.dto.CreateAccountDTO;
import edu.hm.brickstore.account.dto.CreditAccountDTO;
import edu.hm.brickstore.account.dto.DebitAccountDTO;
import edu.hm.brickstore.account.dto.UpdateAccountStatusDTO;
import edu.hm.brickstore.account.dto.UpdateAddressDTO;
import edu.hm.brickstore.account.dto.UpdateCustomerDTO;
import edu.hm.brickstore.account.entity.Account;
import edu.hm.brickstore.account.entity.AccountStatus;
import edu.hm.brickstore.account.event.AccountAddressUpdatedEvent;
import edu.hm.brickstore.account.event.AccountCreatedEvent;
import edu.hm.brickstore.account.event.AccountCustomerUpdatedEvent;
import edu.hm.brickstore.account.event.AccountStatusUpdatedEvent;
import edu.hm.brickstore.account.event.Event;
import edu.hm.brickstore.account.event.MoneyCreditedEvent;
import edu.hm.brickstore.account.event.MoneyDebitedEvent;
import edu.hm.brickstore.account.repository.EventRepository;
import edu.hm.brickstore.error.AccountDeactivatedException;
import edu.hm.brickstore.error.BalanceInsufficientException;
import edu.hm.brickstore.messaging.service.MessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.math.BigDecimal;

@Slf4j
@Service
@Transactional
public class AccountCommandService {

    private final SequenceGeneratorService sequenceGenerator;
    private final AccountQueryService accountQueryService;
    private final EventRepository eventRepository;
    private final MessagingService messagingService;

    public AccountCommandService(SequenceGeneratorService sequenceGenerator, AccountQueryService accountQueryService, EventRepository eventRepository, MessagingService messagingService) {
        this.sequenceGenerator = sequenceGenerator;
        this.accountQueryService = accountQueryService;
        this.eventRepository = eventRepository;
        this.messagingService = messagingService;
    }

    public CreateAccountDTO createAccountCommand(CreateAccountDTO createAccountDTO) {
        log.info("Create new account for customer={} with address={}", createAccountDTO.getCustomer(), createAccountDTO.getAddress());
        long accountId = sequenceGenerator.generateSequence(Account.SEQUENCE_NAME);
        createAccountDTO.setId(accountId);

        AccountCreatedEvent event = new AccountCreatedEvent(createAccountDTO.getId(), createAccountDTO);
        eventRepository.save(event);

        messagingService.sendMessage(event, "account.created");

        return createAccountDTO;
    }

    public void debitMoneyFromAccount(long accountId, DebitAccountDTO debitAccountDTO) {
        log.info("Debit {} from accountId={}", debitAccountDTO.getDebitAmount(), accountId);
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
        messagingService.sendMessage(event, "account.balance.debited");
    }

    public void creditMoneyToAccount(long accountId, CreditAccountDTO creditAccountDTO) {
        log.info("Credit {} to accountId={}", creditAccountDTO.getCreditAmount(), accountId);
        Account account = accountQueryService.getAccount(accountId);
        if (account.getStatus() == AccountStatus.DEACTIVATED) {
            throw new AccountDeactivatedException();
        }
        MoneyCreditedEvent event = new MoneyCreditedEvent(accountId, creditAccountDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "account.balance.credited");
    }

    public void updateStatus(long accountId, @Valid UpdateAccountStatusDTO updateAccountStatusDTO) {
        log.info("Update status for accountId={} to {}", accountId, updateAccountStatusDTO.getStatus());
        Account account = accountQueryService.getAccount(accountId);
        Event<UpdateAccountStatusDTO> event = new AccountStatusUpdatedEvent(accountId, updateAccountStatusDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "account.status.updated");
    }

    public void updateCustomer(long accountId, UpdateCustomerDTO updateCustomerDTO) {
        log.info("Update customer information for accountId={}", accountId);
        Account account = accountQueryService.getAccount(accountId);
        AccountCustomerUpdatedEvent event = new AccountCustomerUpdatedEvent(accountId, updateCustomerDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "account.customer.updated");
    }

    public void updateAddress(long accountId, UpdateAddressDTO updateAddressDTO) {
        log.info("Update address information for accountId={}", accountId);
        Account account = accountQueryService.getAccount(accountId);
        AccountAddressUpdatedEvent event = new AccountAddressUpdatedEvent(accountId, updateAddressDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "account.address.updated");
    }
}
