package com.memoryleak.bloodbank.controller;

import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.Location;
import com.memoryleak.bloodbank.model.User;
import com.memoryleak.bloodbank.notification.EmailNotificationHandler;
import com.memoryleak.bloodbank.repository.GeneralUserRepository;
import com.memoryleak.bloodbank.repository.LocationRepository;
import com.memoryleak.bloodbank.repository.UserRepository;
import com.memoryleak.bloodbank.service.UserService;
import com.memoryleak.bloodbank.util.JwtTokenUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
public class RegistrationController {

    @Value("${BASE_URL:http://localhost:8080}")
    String BASE_URL;

    private static final Pattern usernameMatcher = Pattern.compile("^[A-Za-z]\\w{5,29}$");
    private static final Pattern passwordMatcher = Pattern.compile("^.{8,20}$");
    private static final Pattern emailMatcher = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}");

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GeneralUserRepository generalUserRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    EmailNotificationHandler emailNotificationHandler;


    private boolean validUser(User user) {
        String username = user.getUsername(), email = user.getEmail(), password = user.getPassword();
        return usernameMatcher.matcher(username).matches() &&
                passwordMatcher.matcher(password).matches() &&
                emailMatcher.matcher(email).matches();
    }

    @PostMapping(path = "/register/user")
    public ResponseEntity<String> registerUser(@RequestBody String requestString) throws IOException {
        JSONObject requestData = new JSONObject(requestString);

        Location location = new Location();
        // If no Location is set, Invalid Location
        location.setLongitude(requestData.optDouble("longitude", -200));
        location.setLatitude(requestData.optDouble("latitude", -100));
        locationRepository.save(location);

        User user = new User();
        user.setUsername(requestData.getString("username"));
        user.setPassword(requestData.getString("password"));
        user.setEmail(requestData.getString("email"));
        user.setLocation(location);

        if (validUser(user) && userService.findByUsername(user.getUsername()) == null) {
            userService.save(user);

            GeneralUser generalUser = new GeneralUser();
            generalUser.setUser(user);
            generalUser.setBloodGroup(requestData.getString("bloodGroup").toUpperCase());
            generalUser.setName(requestData.getString("name"));
            generalUser.setImageURL(requestData.optString("imageURL"));
            generalUser.setAbout(requestData.getString("about"));
            generalUser.setActiveDonor(requestData.getBoolean("active"));
            generalUser.setLastDonation(new Date(requestData.getLong("lastDonation")));

            generalUserRepository.save(generalUser);

            String jwtVerification = jwtTokenUtil.generateVerifyToken(user.getUsername(), user.getEmail(), "activate");
            emailNotificationHandler.sendEmail(
                    Collections.singletonList(user.getEmail()),
                    "Confirm Your Account",
                    "Go to the link below to activate your BloodBook Account\n"+
                            BASE_URL+"/register/activate/"+jwtVerification
                    );
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else
            return ResponseEntity.badRequest().build();
    }

    @GetMapping(path = "/register/activate/{jwtVerify}")
    public ResponseEntity<String> activateUser(@PathVariable String jwtVerify) {
        String username = jwtTokenUtil.validateAndGetUsernameFromToken(jwtVerify, "activate");

        if (username == null) return
                ResponseEntity.badRequest().build();

        User user = userService.findByUsername(username);
        user.setActive(true);
        userRepository.save(user);
        return ResponseEntity.ok("Account Activated!");
    }

    @PostMapping(path = "/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody String requestString) throws IOException {
        JSONObject requestData = new JSONObject(requestString);

        User user = userRepository.findUserByUsernameIgnoreCase(requestData.getString("username"));

        if (user!=null) {
            String jwtVerification = jwtTokenUtil.generateVerifyToken(user.getUsername(), user.getEmail(), "forgot");
            emailNotificationHandler.sendEmail(
                    Collections.singletonList(user.getEmail()),
                    "Confirm Password Reset",
                    "Go to the link below to activate your BloodBook Account\n"+
                            BASE_URL+"/forgot/"+jwtVerification
            );
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } else
            return ResponseEntity.badRequest().build();
    }

    @GetMapping(path = "/forgot/{jwtVerify}")
    public ResponseEntity<String> passwordReset(@PathVariable String jwtVerify) {
        String username = jwtTokenUtil.validateAndGetUsernameFromToken(jwtVerify, "forgot");

        if (username == null) return
                ResponseEntity.badRequest().build();

        User user = userService.findByUsername(username);
        String password = UUID.randomUUID().toString();
        user.setPassword(password);
        userService.save(user);
        return ResponseEntity.ok("Password Reset! Use Password: "+password);
    }
}
