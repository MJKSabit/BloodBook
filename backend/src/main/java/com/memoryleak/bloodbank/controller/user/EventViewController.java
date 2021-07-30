package com.memoryleak.bloodbank.controller.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.Event;
import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.Post;
import com.memoryleak.bloodbank.repository.BloodBankRepository;
import com.memoryleak.bloodbank.repository.EventForUserRepository;
import com.memoryleak.bloodbank.repository.EventRepository;
import com.memoryleak.bloodbank.repository.GeneralUserRepository;
import com.memoryleak.bloodbank.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import static com.memoryleak.bloodbank.controller.user.PostController.PAGE_SIZE;

@RestController
public class EventViewController {

    @Autowired
    EventForUserRepository eventForUserRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    GeneralUserRepository generalUserRepository;

    @Autowired
    BloodBankRepository bloodBankRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @JsonView(View.Public.class)
    @GetMapping("/user/events")
    public Slice<Event> postsForUser(@RequestHeader("Authorization") String bearerToken,
                                     @RequestParam(required = false, defaultValue = "my", name = "for") String filter,
                                     @RequestParam(required = false, defaultValue = "0") int page) {

        switch (filter) {
            case "my":
                GeneralUser generalUser = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7)));
                return eventForUserRepository.findAllEventForUser(generalUser, PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending()));
            case "all":
                return eventRepository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending()));
            default:
                return null;
        }
    }

    @JsonView(View.Public.class)
    @GetMapping("/user/events/{bloodbank}")
    public Slice<Event> postsOfUser(@PathVariable String bloodbank,
                                    @RequestParam(required = false, defaultValue = "0") int page) {
        return eventRepository.findByUser(
                bloodBankRepository.findGeneralUserByUserUsernameIgnoreCase(bloodbank),
                PageRequest.of(page, PAGE_SIZE, Sort.by("posted").descending())
        );
    }

}
