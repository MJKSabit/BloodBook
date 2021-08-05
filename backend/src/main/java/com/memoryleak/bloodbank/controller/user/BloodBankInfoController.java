package com.memoryleak.bloodbank.controller.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.BloodBank;
import com.memoryleak.bloodbank.model.Event;
import com.memoryleak.bloodbank.service.BloodBankService;
import com.memoryleak.bloodbank.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BloodBankInfoController {

    @Autowired
    BloodBankService bloodBankService;

    @Autowired
    EventService eventService;

    @JsonView(View.Public.class)
    @GetMapping("/user/events")
    public Slice<Event> postsForUser(@RequestHeader("Authorization") String bearerToken,
                                     @RequestParam(required = false, defaultValue = "my", name = "for") String filter,
                                     @RequestParam(required = false, defaultValue = "0") int page) {

        switch (filter) {
            case "my":
                String jwt = bearerToken.substring(7);
                return eventService.eventsForUser(jwt, page);
            case "all":
                return eventService.allEvents(page);
            default:
                return null;
        }
    }

    @JsonView(View.Public.class)
    @GetMapping("/explore")
    List<BloodBank> exploreBloodBank(@RequestHeader("Authorization") String bearerToken) {
        String jwt = bearerToken.substring(7);
        return bloodBankService.bloodBanksNearUser(jwt);
    }

}
