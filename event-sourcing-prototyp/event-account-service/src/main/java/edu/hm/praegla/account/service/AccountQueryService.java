package edu.hm.praegla.account.service;

import edu.hm.praegla.account.entity.Account;
import edu.hm.praegla.account.error.EntityNotFoundException;
import edu.hm.praegla.account.event.Event;
import edu.hm.praegla.account.repository.AccountRepository;
import edu.hm.praegla.account.repository.EventRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        return accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException(Account.class, "id", accountId));
    }

    public Iterable<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public List<Event> getEventsForAccountId(long accountId) {
        return eventRepository.findAllByAggregateId(accountId, Sort.by(Sort.Direction.ASC, "timestamp"));
    }

    public Event getEventById(String eventId) {
        Optional<Event> byId = eventRepository.findById(eventId);
        return byId.orElseThrow(() -> new EntityNotFoundException(Event.class, "eventId", eventId));
    }

}
