package com.memoryleak.bloodbank.controller.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.service.AuthService;
import com.memoryleak.bloodbank.service.GeneralUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final static Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    GeneralUserService generalUserService;

    @Autowired
    AuthService authService;

    @GetMapping("/user/profile/{username}")
    @JsonView(View.ExtendedPublic.class)
    public ResponseEntity<GeneralUser> profile(@PathVariable String username) {
        GeneralUser user = generalUserService.get(username);
        if (user == null)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(user);
    }

    @GetMapping("/user/profile")
    @JsonView(View.Private.class)
    public GeneralUser myProfile(@RequestHeader("Authorization") String bearerToken) {
        return generalUserService.getFromJWT(bearerToken.substring(7));
    }

    @PostMapping("/user/change-profile")
    @JsonView(View.Private.class)
    public GeneralUser changeGeneralSettings(@RequestHeader("Authorization") String bearerToken,
                                             @RequestBody String requestText) {
        String jwt = bearerToken.substring(7);
        return generalUserService.update(jwt, new JSONObject(requestText));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changeUserPassword(@RequestHeader("Authorization") String bearerToken,
                                             @RequestBody String requestText) {
        String jwt = bearerToken.substring(7);
        if (authService.changePassword(jwt, new JSONObject(requestText)))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/user/messenger-token")
    public String getMessengerToken(@RequestHeader("Authorization") String bearerToken) {
        String jwt = bearerToken.substring(7);
        return generalUserService.generateMessengerToken(jwt);
    }

    @DeleteMapping("/user/messenger-token")
    public ResponseEntity<?> deleteMessengerToken(@RequestHeader("Authorization") String bearerToken) {
        String jwt = bearerToken.substring(7);
        generalUserService.deleteMessengerToken(jwt);
        return ResponseEntity.ok().build();
    }
}
