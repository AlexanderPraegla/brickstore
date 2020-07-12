package edu.hm.praegla.account.messaging;

import edu.hm.praegla.account.dto.CreateAccountDTO;
import edu.hm.praegla.account.dto.UpdateCustomerDTO;
import edu.hm.praegla.account.entity.Account;
import edu.hm.praegla.account.entity.AccountStatus;
import edu.hm.praegla.account.event.AccountCreatedEvent;
import edu.hm.praegla.account.event.AccountCustomerUpdatedEvent;
import edu.hm.praegla.account.event.AccountStatusUpdatedEvent;
import edu.hm.praegla.account.repository.AccountRepository;
import edu.hm.praegla.account.service.AccountQueryService;
import edu.hm.praegla.shoppingcart.entity.ShoppingCart;
import edu.hm.praegla.shoppingcart.repository.ShoppingCartRepository;
import edu.hm.praegla.shoppingcart.service.SequenceGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Slf4j
@Component
@RabbitListener(queues = AccountMessagingConfig.ACCOUNT_SERVICE_QUEUE)
public class AccountMessageHandler {

    private final AccountRepository accountRepository;
    private final AccountQueryService accountQueryService;
    private final ShoppingCartRepository shoppingCartRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    public AccountMessageHandler(AccountRepository accountRepository, AccountQueryService accountQueryService, ShoppingCartRepository shoppingCartRepository, SequenceGeneratorService sequenceGeneratorService) {
        this.accountRepository = accountRepository;
        this.accountQueryService = accountQueryService;
        this.shoppingCartRepository = shoppingCartRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
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

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(sequenceGeneratorService.generateSequence(ShoppingCart.SEQUENCE_NAME));
        shoppingCart.setAccountId(accountId);
        shoppingCart.setCustomerName(customerName);
        shoppingCartRepository.save(shoppingCart);
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
