package edu.hm.praegla.account.controller;

import edu.hm.praegla.account.event.Event;
import edu.hm.praegla.account.service.AccountQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "events", produces = {"application/json"})
@Tag(name = "Event API")
public class EventController {

    private final AccountQueryService accountQueryService;

    public EventController(AccountQueryService accountQueryService) {
        this.accountQueryService = accountQueryService;
    }

    @GetMapping("/account/{accountId}")
    public List<Event> getAccountEvents(@PathVariable long accountId) {
        return accountQueryService.getEventsForAccountId(accountId);
    }

    @GetMapping
    public List<Event> getEvent() {
        return accountQueryService.getAllEvents();
    }

    @GetMapping("/{eventId}")
    public Event getEvent(@PathVariable String eventId) {
        return accountQueryService.getEventById(eventId);
    }

}
