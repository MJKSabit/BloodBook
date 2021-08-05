package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.model.*;
import com.memoryleak.bloodbank.repository.EventForUserRepository;
import com.memoryleak.bloodbank.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.memoryleak.bloodbank.controller.user.PostController.PAGE_SIZE;

@Service
public class EventService {
    public final static int PAGE_SIZE = 30;

    @Value("${FRONTEND_URL:https://blood-book.netlify.app}")
    String FRONTEND_URL;

    @Autowired
    BloodBankService bloodBankService;

    @Autowired
    GeneralUserService generalUserService;

    @Autowired
    LocationService locationService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventForUserRepository eventForUserRepository;

    @Autowired
    UserNotificationService notificationService;

    public Slice<Event> eventsByBloodBank(String username, int page) {
        return eventRepository.findByUser(
                bloodBankService.get(username),
                PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending())
        );
    }

    @Transactional
    public Event create(String jwt, Event event, boolean notify) {
        BloodBank user = bloodBankService.getFromJWT(jwt);

        Location location = event.getLocation();
        location.setId(null);
        locationService.save(location);

        event.setId(null);
        event.setUser(user);
        event.setPosted(new Date());

        eventRepository.save(event);

        // TODO : IN NEW THREAD
        eventPersonalization(event, notify);

        return event;
    }


    private void eventPersonalization(Event event, boolean notify) {
        List<GeneralUser> users = generalUserService.getMatchingEvent(event);

        List<String> emails = new ArrayList<>();
        List<String> messengerId = new ArrayList<>();

        for (GeneralUser user : users) {
            eventForUserRepository.save(new GeneralUserToEvent(user, event));

            if (notify) {
                emails.add(user.getUser().getEmail());
                if (user.getFacebook() != null)
                    messengerId.add(user.getFacebook());
            }
        }

        if (notify) {
            String subjectText = sendSubject(event);
            String postText = sendText(event);
            notificationService.sendFacebookMessage(messengerId, postText);
            notificationService.sendEmail(emails, subjectText, postText);
        }
    }

    private String getMapLink(double lat, double lng) {
        return "https://www.google.com/maps/search/?api=1&query="+lat+","+lng;
    }

    private String getEventLink(Event event) { return  FRONTEND_URL+"/user/event/"+event.getId(); }

    private String sendText(Event event) {
        return String.format("Hello BloodBook User,\n" +
            "%s (%s) is going to arrange an event near you\n" +
            "At: %s (Posted At: %s)\n" +
            "Information:\n" +
            "%s\n\n" +
            "Location: %s\n" +
            "Link: %s\n" +
            " - BloodBook",
            event.getUser().getName(), event.getUser().getUser().getEmail(),
            event.getEventDate().toString(), event.getPosted().toString(),
            event.getInfo(),
            getMapLink(event.getLocation().getLatitude(), event.getLocation().getLongitude()),
            getEventLink(event));
    }

    private String sendSubject(Event event) {
        return String.format("Upcoming Event by %s", event.getUser().getName());
    }

    public Event get(long id) {
        return eventRepository.findEventById(id);
    }

    public Slice<Event> eventsForUser(String jwt, int page) {
        GeneralUser generalUser = generalUserService.getFromJWT(jwt);
        return eventForUserRepository.findAllEventForUser(
                generalUser, PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending()));
    }

    public Slice<Event> allEvents(int page) {
        return eventRepository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending()));
    }

    @Transactional
    public Event delete(String jwt, long id) throws IllegalAccessError {
        Event event = get(id);
        if (event == null) return null;

        BloodBank user = bloodBankService.getFromJWT(jwt);
        if (user == null) throw new IllegalAccessError();

        // TODO : DELETE QUERY
        List<GeneralUserToEvent> generalUserToEvents = eventForUserRepository.findAllByEvent(event);
        for (GeneralUserToEvent generalUserToEvent : generalUserToEvents)
            eventForUserRepository.delete(generalUserToEvent);

        eventRepository.delete(event);
        return event;
    }
}
