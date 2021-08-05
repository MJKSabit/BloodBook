package com.memoryleak.bloodbank.controller.bank;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.Event;
import com.memoryleak.bloodbank.service.EventService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
public class EventController {

    private final static Logger logger = LogManager.getLogger(EventController.class);

    @Autowired
    EventService eventService;

    @GetMapping("/bloodbank/events/{username}")
    @JsonView(View.Public.class)
    public Slice<Event> eventsOfBloodBank(@PathVariable String username, @RequestParam(required = false, defaultValue = "0") int page) {
        return eventService.eventsByBloodBank(username, page);
    }

    @Transactional
    @JsonView(View.Public.class)
    @PostMapping("/bloodbank/event")
    public ResponseEntity<Event> createEvent(@RequestHeader("Authorization") String bearerToken,
                                             @RequestBody Event event,
                                             @RequestParam(required = false, defaultValue = "true") boolean notify) {
        String jwt = bearerToken.substring(7);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                eventService.create(jwt, event, notify)
        );
    }

    @JsonView(View.Public.class)
    @DeleteMapping("/bloodbank/event/{id}")
    public ResponseEntity<Event> deleteEvent(@RequestHeader("Authorization") String bearerToken, @PathVariable long id) {
        String jwt = bearerToken.substring(7);
        try {
            Event event = eventService.delete(jwt, id);
            if (event == null)
                return ResponseEntity.notFound().build();
            else
                return ResponseEntity.ok(event);
        } catch (IllegalAccessError ignore) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @JsonView(View.Public.class)
    @GetMapping("/bloodbank/event/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable long id) {
        Event event = eventService.get(id);

        if (event == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(event);
    }
}
