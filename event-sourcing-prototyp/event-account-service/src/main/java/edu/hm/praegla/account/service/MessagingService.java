package edu.hm.praegla.account.service;

import edu.hm.praegla.account.event.Event;
import edu.hm.praegla.account.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class MessagingService {

    private final AccountRepository accountRepository;
    private final AccountQueryService accountQueryService;
    private final RabbitTemplate rabbitTemplate;
    private final Exchange exchange;

    public MessagingService(AccountRepository accountRepository, AccountQueryService accountQueryService, RabbitTemplate rabbitTemplate, Exchange exchange) {
        this.accountRepository = accountRepository;
        this.accountQueryService = accountQueryService;
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void sendMessage(Event<?> event) {
        rabbitTemplate.convertAndSend(exchange.getName(), "account." + event.getEventType(), event);
    }

//    @RabbitListener(queues = MessagingRabbitMqConfig.ACCOUNT_SERVICE_QUEUE)
//    public void listenAccountServicQueue(@Payload Event<?> event, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key) {
//        log.info("Event received via rounting key='{}': {}", key, event);
//        switch (event.getEventType()) {
//            case "AccountCreatedEvent":
//                handleAccountCreatedEvent((Event<Account>) event);
//                break;
//            case "AccountStatusUpdatedEvent":
//                handleAccountStatusUpdatedEvent((Event<UpdateAccountStatusDTO>) event);
//                break;
//            case "MoneyCreditedEvent":
//                handleMoneyCreditedEvent((Event<CreditAccountDTO>) event);
//                break;
//            case "MoneyDebitedEvent":
//                handleMoneyDebitedEvent((Event<DebitAccountDTO>) event);
//                break;
//            case "AccountCustomerUpdatedEvent":
//                handleAccountCustomerUpdatedEvent((Event<UpdateCustomerDTO>) event);
//                break;
//            case "AccountAddressUpdatedEvent":
//                handleAccountAddressUpdatedEvent((Event<UpdateAddressDTO>) event);
//                break;
//            default:
//                log.error("There is no handler for the event type '{}}'", event.getEventType());
//        }
//    }
//
//    public void handleAccountCreatedEvent(Event<Account> event) {
//        accountRepository.save(event.getPayload());
//    }
//
//    public void handleAccountStatusUpdatedEvent(Event<UpdateAccountStatusDTO> event) {
//        Account account = accountQueryService.getAccount(event.getAggregateId());
//        account.setStatus(event.getPayload().getStatus());
//        accountRepository.save(account);
//    }
//
//    public void handleAccountCustomerUpdatedEvent(Event<UpdateCustomerDTO> event) {
//        Account account = accountQueryService.getAccount(event.getAggregateId());
//        Account.Customer customer = account.getCustomer();
//        UpdateCustomerDTO payload = event.getPayload();
//        customer.setFirstname(payload.getFirstname());
//        customer.setLastname(payload.getLastname());
//        customer.setEmail(payload.getEmail());
//        account.setCustomer(customer);
//        accountRepository.save(account);
//    }
//
//    public void handleAccountAddressUpdatedEvent(Event<UpdateAddressDTO> event) {
//        Account account = accountQueryService.getAccount(event.getAggregateId());
//        Account.Address address = account.getAddress();
//        UpdateAddressDTO payload = event.getPayload();
//        address.setCity(payload.getCity());
//        address.setStreet(payload.getStreet());
//        address.setPostalCode(payload.getPostalCode());
//        account.setAddress(address);
//        accountRepository.save(account);
//    }
//
//    public void handleMoneyCreditedEvent(Event<CreditAccountDTO> event) {
//        Account account = accountQueryService.getAccount(event.getAggregateId());
//        BigDecimal balance = account.getBalance();
//        account.setBalance(balance.add(event.getPayload().getCreditAmount()));
//
//        if (account.getStatus() == AccountStatus.CREATED) {
//            account.setStatus(AccountStatus.ACTIVATED);
//        }
//        accountRepository.save(account);
//    }
//
//    public void handleMoneyDebitedEvent(Event<DebitAccountDTO> event) {
//        Account account = accountQueryService.getAccount(event.getAggregateId());
//        BigDecimal balance = account.getBalance();
//        account.setBalance(balance.subtract(event.getPayload().getDebitAmount()));
//        accountRepository.save(account);
//    }
}
