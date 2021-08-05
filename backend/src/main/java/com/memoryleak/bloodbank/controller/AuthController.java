package com.memoryleak.bloodbank.controller;

import com.memoryleak.bloodbank.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AuthController {

    private final static Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<String> login(@RequestBody String jsonString) {
        try {
            JSONObject response = authService.authenticate(new JSONObject(jsonString));
            return ResponseEntity.ok(response.toString());
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
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

    @PostMapping(path = "/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody String requestString) throws IOException {
        if (authService.forgotPassword(new JSONObject(requestString)))
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        else
            return ResponseEntity.badRequest().build();
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<?> passwordReset(@RequestBody String jwtVerifyStr) {
        if (authService.resetPassword(new JSONObject(jwtVerifyStr)))
            return ResponseEntity.accepted().build();
        else
            return ResponseEntity.badRequest().build();
    }
}
