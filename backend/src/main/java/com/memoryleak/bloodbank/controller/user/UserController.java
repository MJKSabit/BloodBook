package com.memoryleak.bloodbank.controller.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.User;
import com.memoryleak.bloodbank.repository.GeneralUserRepository;
import com.memoryleak.bloodbank.repository.LocationRepository;
import com.memoryleak.bloodbank.repository.UserRepository;
import com.memoryleak.bloodbank.util.JwtTokenUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.memoryleak.bloodbank.controller.RegistrationController.passwordMatcher;

@RestController
public class UserController {
    private final static Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    GeneralUserRepository generalUserRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @GetMapping("/user/profile/{username}")
    @JsonView(View.ExtendedPublic.class)
    public ResponseEntity<GeneralUser> profile(@PathVariable String username) {
        GeneralUser user = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);
        if (user == null)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(user);
    }

    @GetMapping("/user/profile")
    @JsonView(View.Private.class)
    public GeneralUser myProfile(@RequestHeader("Authorization") String bearerToken) {
        String username = jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7));
        return generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);
    }

    @PostMapping("/user/change-profile")
    @JsonView(View.Private.class)
    public GeneralUser changeGeneralSettings(@RequestHeader("Authorization") String bearerToken,
                                             @RequestBody String requestText) {
        String username = jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7));
        GeneralUser generalUser = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);
        User user = generalUser.getUser();

        JSONObject request = new JSONObject(requestText);

        user.getLocation().setLatitude(request.getDouble("latitude"));
        user.getLocation().setLongitude(request.getDouble("longitude"));
        locationRepository.save(user.getLocation());

        generalUser.setName(request.getString("name"));
        generalUser.setImageURL(request.getString("imageURL"));
        generalUser.setLastDonation(new Date(request.getLong("lastDonation")));
        generalUser.setActiveDonor(request.getBoolean("isActiveDonor"));
        generalUser.setAbout(request.getString("about"));

        return generalUserRepository.save(generalUser);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changeUserPassword(@RequestHeader("Authorization") String bearerToken,
                                             @RequestBody String requestText) {
        String username = jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7));
        User user = userRepository.findUserByUsernameIgnoreCase(username);

        JSONObject request = new JSONObject(requestText);
        String oldPassword = request.getString("old");
        String newPassword = request.getString("new");

        if (!passwordMatcher.matcher(newPassword).matches())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(user);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/user/messenger-token")
    public String getMessengerToken(@RequestHeader("Authorization") String bearerToken) {
        String username = jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7));
        GeneralUser generalUser = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);
        User user = generalUser.getUser();
        return jwtTokenUtil.generateVerifyToken(user.getUsername(), user.getEmail(), "messenger");
    }

    @DeleteMapping("/user/messenger-token")
    public ResponseEntity<?> deleteMessengerToken(@RequestHeader("Authorization") String bearerToken) {
        String username = jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7));
        GeneralUser generalUser = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);
        generalUser.setFacebook(null);
        generalUserRepository.save(generalUser);
        return ResponseEntity.ok().build();
    }
}
