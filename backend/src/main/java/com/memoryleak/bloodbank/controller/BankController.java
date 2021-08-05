package com.memoryleak.bloodbank.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.BloodBank;
import com.memoryleak.bloodbank.model.BloodBankBloodCount;
import com.memoryleak.bloodbank.service.BloodBankService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BankController {

    @Autowired
    BloodBankService bankService;

    @JsonView(View.Private.class)
    @GetMapping("/bloodbank/profile/{username}")
    public ResponseEntity<BloodBank> profile(@PathVariable String username) {
        BloodBank user = bankService.get(username);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(user);
    }

    @GetMapping("/bloodbank/profile")
    @JsonView(View.Private.class)
    public ResponseEntity<BloodBank> myProfile(@RequestHeader("Authorization") String bearerToken) {
        BloodBank user = bankService.getFromJWT(bearerToken.substring(7));
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(user);
    }

    @GetMapping("/bloodbank/count/{username}")
    public ResponseEntity<List<BloodBankBloodCount>> count(@PathVariable String username) {
        List<BloodBankBloodCount> bankBloodCounts = bankService.getBloodCount(username);
        if (bankBloodCounts == null)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(bankBloodCounts);
    }

    @GetMapping("/bloodbank/count")
    public ResponseEntity<List<BloodBankBloodCount>> myCount(@RequestHeader("Authorization") String bearerToken) {
        List<BloodBankBloodCount> bankBloodCounts = bankService.getBloodCountFromJWT(bearerToken.substring(7));
        if (bankBloodCounts == null)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(bankBloodCounts);
    }

    @PostMapping("/bloodbank/count")
    public ResponseEntity<List<BloodBankBloodCount>> setMyCount(@RequestHeader("Authorization") String bearerToken,
                                                                @RequestBody String stringBody) {
        List<BloodBankBloodCount> bankBloodCounts = bankService.setBloodCount(
                bearerToken.substring(7), new JSONObject(stringBody)
        );
        return ResponseEntity.ok(bankBloodCounts);
    }

    @PostMapping("/bloodbank/change-profile")
    @JsonView(View.Private.class)
    public ResponseEntity<BloodBank> changeSettings(@RequestHeader("Authorization") String bearerToken,
                                                    @RequestBody String settings) {
        BloodBank bank = bankService.update(bearerToken.substring(7), new JSONObject(settings));
        return ResponseEntity.ok(bank);
    }

    @JsonView(View.Public.class)
    @GetMapping("/explore")
    List<BloodBank> exploreNearbyBloodBank(@RequestHeader("Authorization") String bearerToken) {
        String jwt = bearerToken.substring(7);
        return bankService.bloodBanksNearUser(jwt);
    }
}
