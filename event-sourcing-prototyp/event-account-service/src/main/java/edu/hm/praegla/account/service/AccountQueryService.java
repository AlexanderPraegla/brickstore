package edu.hm.praegla.account.service;

import edu.hm.praegla.account.entity.Account;
import edu.hm.praegla.account.error.EntityNotFoundException;
import edu.hm.praegla.account.event.Event;
import edu.hm.praegla.account.repository.AccountRepository;
import edu.hm.praegla.account.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class AccountQueryService {

    private final AccountRepository accountRepository;
    private final EventRepository eventRepository;

    public AccountQueryService(AccountRepository accountRepository, EventRepository eventRepository) {
        this.accountRepository = accountRepository;
        this.eventRepository = eventRepository;
    }


    public Account getAccount(long accountId) {
        log.info("Get account for accountId={}", accountId);
        return accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException(Account.class, "id", accountId));
    }

    public Iterable<Account> getAccounts() {
        log.info("Get all accounts");
        return accountRepository.findAll();
    }

    public List<Event> getEventsForAccountId(long accountId) {
        log.info("Get all events for accountId={}", accountId);
        return eventRepository.findAllByAggregateId(accountId, Sort.by(Sort.Direction.ASC, "timestamp"));
    }

    public List<Event> getAllEvents() {
        log.info("Get all saved events");
        return eventRepository.findAll();
    }

    public Event getEventById(String eventId) {
        log.info("Get event for eventId={}", eventId);
        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(Event.class, "eventId", eventId));
    }

}
