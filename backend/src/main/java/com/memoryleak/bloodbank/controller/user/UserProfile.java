package com.memoryleak.bloodbank.controller.user;

import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.repository.GeneralUserRepository;
import com.memoryleak.bloodbank.repository.UserRepository;
import com.memoryleak.bloodbank.util.JwtTokenUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class UserProfile {
    private final static Logger logger = LogManager.getLogger(UserProfile.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    GeneralUserRepository generalUserRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @GetMapping("/user/{username}")
    public ResponseEntity<GeneralUser> profile(@PathVariable String username) {
        GeneralUser user = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user")
    public RedirectView myProfile(@RequestHeader("Authorization") String bearerToken) {
        String username = jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7));
        return new RedirectView("/user/"+username);
    }
}
