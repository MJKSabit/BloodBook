package com.memoryleak.bloodbank.controller.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.*;
import com.memoryleak.bloodbank.repository.*;
import com.memoryleak.bloodbank.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.memoryleak.bloodbank.controller.user.PostController.PAGE_SIZE;

@RestController
public class BloodBankInfoController {

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

    @Autowired
    private UserRepository userRepository;

    @JsonView(View.Public.class)
    @GetMapping("/user/events")
    public Slice<Event> postsForUser(@RequestHeader("Authorization") String bearerToken,
                                     @RequestParam(required = false, defaultValue = "my", name = "for") String filter,
                                     @RequestParam(required = false, defaultValue = "0") int page) {

        switch (filter) {
            case "my":
                GeneralUser generalUser = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(
                        jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7))
                );
                return eventForUserRepository.findAllEventForUser(generalUser, PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending()));
            case "all":
                return eventRepository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending()));
            default:
                return null;
        }
    }

    @JsonView(View.Public.class)
    @GetMapping("/explore")
    List<BloodBank> exploreBloodBank(@RequestHeader("Authorization") String bearerToken) {
        User user = userRepository.findUserByUsernameIgnoreCase(
                jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7))
        );
        Location location = user.getLocation();
        return bloodBankRepository.exploreNearbyBloodBank(location.getLatitude(), location.getLongitude());
    }

}
