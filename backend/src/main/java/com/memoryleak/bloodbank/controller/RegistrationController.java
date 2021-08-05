package com.memoryleak.bloodbank.controller;

import com.memoryleak.bloodbank.service.AuthService;
import com.memoryleak.bloodbank.service.ValidationException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class RegistrationController {

    @Value("${BASE_URL:http://localhost:8080}")
    String BASE_URL;

    @Value("${FRONTEND_URL:http://localhost:3000}")
    String FRONTEND_URL;

    @Autowired
    AuthService authService;

    @PostMapping(path = "/register/user")
    public ResponseEntity<?> registerUser(@RequestBody String requestString) throws IOException {
        try {
            authService.registerNewUser(new JSONObject(requestString));
            return ResponseEntity.ok().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path = "/register/bloodbank")
    public ResponseEntity<String> registerBloodBank(@RequestBody String requestString) {
        try {
            authService.registerNewBloodBank(new JSONObject(requestString));
            return ResponseEntity.ok().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path = "/activate")
    public ResponseEntity<?> activateUser(@RequestBody String jwtVerifyStr) {
        if (authService.activateUser(new JSONObject(jwtVerifyStr)))
            return ResponseEntity.accepted().build();
        else
            return ResponseEntity.badRequest().build();
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
