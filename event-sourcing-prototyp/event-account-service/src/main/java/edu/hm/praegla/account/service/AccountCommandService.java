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
import edu.hm.praegla.account.error.BrickstoreException;
import edu.hm.praegla.account.event.AccountAddressUpdatedEvent;
import edu.hm.praegla.account.event.AccountCreatedEvent;
import edu.hm.praegla.account.event.AccountCustomerUpdatedEvent;
import edu.hm.praegla.account.event.AccountStatusUpdatedEvent;
import edu.hm.praegla.account.event.Event;
import edu.hm.praegla.account.event.MoneyCreditedEvent;
import edu.hm.praegla.account.event.MoneyDebitedEvent;
import edu.hm.praegla.account.repository.EventRepository;
import edu.hm.praegla.messaging.service.MessagingService;
import edu.hm.praegla.order.entity.Order;
import edu.hm.praegla.order.event.OrderDebitAccountFailedEvent;
import edu.hm.praegla.order.event.OrderDebitAccountSucceededEvent;
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
    private final MessagingService messagingService;

    public AccountCommandService(SequenceGeneratorService sequenceGenerator, AccountQueryService accountQueryService, EventRepository eventRepository, MessagingService messagingService) {
        this.sequenceGenerator = sequenceGenerator;
        this.accountQueryService = accountQueryService;
        this.eventRepository = eventRepository;
        this.messagingService = messagingService;
    }

    public CreateAccountDTO createAccountCommand(CreateAccountDTO createAccountDTO) {
        long accountId = sequenceGenerator.generateSequence(Account.SEQUENCE_NAME);
        createAccountDTO.setId(accountId);

        AccountCreatedEvent event = new AccountCreatedEvent(createAccountDTO.getId(), createAccountDTO);
        eventRepository.save(event);

        messagingService.sendMessage(event, "account.created");

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
        messagingService.sendMessage(event, "account.balance.debited");
    }

    public void creditMoneyToAccount(long accountId, CreditAccountDTO creditAccountDTO) {
        Account account = accountQueryService.getAccount(accountId);
        if (account.getStatus() == AccountStatus.DEACTIVATED) {
            throw new AccountDeactivatedException();
        }
        MoneyCreditedEvent event = new MoneyCreditedEvent(accountId, creditAccountDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "account.balance.credited");
    }

    public void updateStatus(long accountId, @Valid UpdateAccountStatusDTO updateAccountStatusDTO) {
        Account account = accountQueryService.getAccount(accountId);
        Event<UpdateAccountStatusDTO> event = new AccountStatusUpdatedEvent(accountId, updateAccountStatusDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "account.status.updated");
    }

    public void updateCustomer(long accountId, UpdateCustomerDTO updateCustomerDTO) {
        Account account = accountQueryService.getAccount(accountId);
        AccountCustomerUpdatedEvent event = new AccountCustomerUpdatedEvent(accountId, updateCustomerDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "account.customer.updated");
    }

    public void updateAddress(long accountId, UpdateAddressDTO updateAddressDTO) {
        Account account = accountQueryService.getAccount(accountId);
        AccountAddressUpdatedEvent event = new AccountAddressUpdatedEvent(accountId, updateAddressDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "account.address.updated");
    }

    public void debitAccountForOrder(Order order) {
        Event<?> event = null;
        try {
            debitMoneyFromAccount(order.getAccountId(), new DebitAccountDTO(order.getTotal()));
            event = new OrderDebitAccountSucceededEvent(order.getId(), order);
            messagingService.sendMessage(event, "order.account.debited.succeeded");
        } catch (BrickstoreException e) {
            order.setErrorCode(e.getResponseCode());
            event = new OrderDebitAccountFailedEvent(order.getId(), order);
            messagingService.sendMessage(event, "order.account.debit.failed");
        }
    }
}
