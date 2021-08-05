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
import org.springframework.web.bind.annotation.RestController;

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
}
