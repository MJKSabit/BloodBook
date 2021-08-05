package com.memoryleak.bloodbank.controller.bank;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.*;
import com.memoryleak.bloodbank.service.UserNotificationService;
import com.memoryleak.bloodbank.repository.*;
import com.memoryleak.bloodbank.util.JwtTokenUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.memoryleak.bloodbank.controller.user.PostController.PAGE_SIZE;

@RestController
public class EventController {

    private final static Logger logger = LogManager.getLogger(EventController.class);

    @Autowired
    EventRepository eventRepository;

    @Autowired
    BloodBankRepository bloodBankRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    GeneralUserRepository generalUserRepository;

    @Autowired
    EventForUserRepository eventForUserRepository;

    @Autowired
    UserNotificationService userNotificationService;

    private BloodBank getUser(String bearerToken) {
        String username = jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7));
        return bloodBankRepository.findBloodBankByUserUsernameIgnoreCase(username);
    }

    @GetMapping("/bloodbank/events/{username}")
    @JsonView(View.Public.class)
    public Slice<Event> eventsOfUser(@PathVariable String username, @RequestParam(required = false, defaultValue = "0") int page) {
        return eventRepository.findByUser(
                bloodBankRepository.findBloodBankByUserUsernameIgnoreCase(username),
                PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending())
        );
    }

    @Transactional
    @JsonView(View.Public.class)
    @PostMapping("/bloodbank/event")
    public ResponseEntity<Event> createEvent(@RequestHeader("Authorization") String bearerToken,
                                             @RequestBody Event event,
                                             @RequestParam(required = false, defaultValue = "true") boolean notify) {
        BloodBank user = getUser(bearerToken);

        if (event == null)
            return ResponseEntity.badRequest().build();

        event.setId(null);

        Location location = event.getLocation();
        location.setId(null);
        location = locationRepository.save(location);

        event.setLocation(location);
        event.setUser(user);
        event.setPosted(new Date());
        eventRepository.save(event);

        if (notify) {
            List<GeneralUser> users = generalUserRepository.getMatchEventRequirement(
                    location.getLatitude(),
                    location.getLongitude());
            userNotificationService.notifyUsers(event, users);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @JsonView(View.Public.class)
    @DeleteMapping("/bloodbank/event/{id}")
    public ResponseEntity<Event> deleteEvent(@RequestHeader("Authorization") String bearerToken, @PathVariable long id) {
        Event event = eventRepository.findEventById(id);

        if (event == null)
            return ResponseEntity.notFound().build();

        if (event.getUser().equals(getUser(bearerToken))) {
            List<GeneralUserToEvent> generalUserToEvents = eventForUserRepository.findAllByEvent(event);

            for (GeneralUserToEvent generalUserToEvent: generalUserToEvents)
                eventForUserRepository.delete(generalUserToEvent);

            eventRepository.delete(event);
            return ResponseEntity.ok(event);
        } else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    @JsonView(View.Public.class)
    @GetMapping("/bloodbank/event/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable long id) {
        Event event = eventRepository.findEventById(id);

        if (event == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(event);
    }
}
