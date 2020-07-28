package edu.hm.brickstore.account.eventhandler;

import edu.hm.brickstore.account.dto.CreateAccountDTO;
import edu.hm.brickstore.account.dto.UpdateCustomerDTO;
import edu.hm.brickstore.account.entity.Account;
import edu.hm.brickstore.account.entity.AccountStatus;
import edu.hm.brickstore.account.event.AccountCreatedEvent;
import edu.hm.brickstore.account.event.AccountCustomerUpdatedEvent;
import edu.hm.brickstore.account.event.AccountStatusUpdatedEvent;
import edu.hm.brickstore.account.repository.AccountRepository;
import edu.hm.brickstore.account.service.AccountQueryService;
import edu.hm.brickstore.messaging.config.MessagingRabbitMqConfig;
import edu.hm.brickstore.shoppingcart.entity.ShoppingCart;
import edu.hm.brickstore.shoppingcart.repository.ShoppingCartRepository;
import edu.hm.brickstore.shoppingcart.service.SequenceGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * Command event handler for all external events from the account-service concerning the order-service
 */
@Slf4j
@Component
@RabbitListener(queues = MessagingRabbitMqConfig.ACCOUNT_TO_SHOPPING_CART_QUEUE)
public class AccountCommandEventHandler {

    private final AccountRepository accountRepository;
    private final AccountQueryService accountQueryService;

    public AccountCommandEventHandler(AccountRepository accountRepository, AccountQueryService accountQueryService) {
        this.accountRepository = accountRepository;
        this.accountQueryService = accountQueryService;
    }

    @RabbitHandler
    public void process(@Payload AccountCreatedEvent event) {
        log.info("Received AccountCreatedEvent: {}", event);

        CreateAccountDTO payload = event.getPayload();
        @NotNull String customerName = createCustomerName(payload);

        Account account = new Account();
        long accountId = payload.getId();
        account.setAccountId(accountId);
        account.setCustomerName(customerName);
        account.setStatus(AccountStatus.CREATED);
        accountRepository.save(account);
    }

    @RabbitHandler
    public void process(@Payload AccountCustomerUpdatedEvent event) {
        log.info("Received AccountCustomerUpdatedEvent: {}", event);

        Account account = accountQueryService.getAccount(event.getAggregateId());
        UpdateCustomerDTO payload = event.getPayload();
        String customerName = createCustomerName(payload);
        account.setCustomerName(customerName);
        accountRepository.save(account);
    }

    @RabbitHandler
    public void process(@Payload AccountStatusUpdatedEvent event) {
        log.info("Received AccountStatusUpdatedEvent: {}", event);

        Account account = accountQueryService.getAccount(event.getAggregateId());
        account.setStatus(event.getPayload().getStatus());
        accountRepository.save(account);
    }

    private String createCustomerName(UpdateCustomerDTO payload) {
        return payload.getFirstname() + payload.getLastname();
    }

    private String createCustomerName(CreateAccountDTO payload) {
        String firstname = payload.getCustomer().getFirstname();
        String lastname = payload.getCustomer().getLastname();
        return firstname + " " + lastname;
    }
}
