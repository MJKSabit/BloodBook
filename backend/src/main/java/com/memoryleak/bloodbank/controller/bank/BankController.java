package com.memoryleak.bloodbank.controller.bank;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.BloodBank;
import com.memoryleak.bloodbank.model.BloodBankBloodCount;
import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.repository.BloodBankBloodCountRepository;
import com.memoryleak.bloodbank.repository.BloodBankRepository;
import com.memoryleak.bloodbank.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
public class BankController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    BloodBankRepository bloodBankRepository;

    @Autowired
    BloodBankBloodCountRepository bloodBankBloodCountRepository;

    @JsonView(View.Private.class)
    @GetMapping("/bloodbank/profile/{username}")
    public ResponseEntity<BloodBank> profile(@PathVariable String username) {
        BloodBank user = bloodBankRepository.findGeneralUserByUserUsernameIgnoreCase(username);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(user);
    }

    @GetMapping("/bloodbank/profile")
    @JsonView(View.Private.class)
    public ResponseEntity<BloodBank> myProfile(@RequestHeader("Authorization") String bearerToken) {
        String username = jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7));
        return profile(username);
    }

    @GetMapping("/bloodbank/count/{username}")
    public ResponseEntity<List<BloodBankBloodCount>> count(@PathVariable String username) {
        BloodBank user = bloodBankRepository.findGeneralUserByUserUsernameIgnoreCase(username);
        if (user == null)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<BloodBankBloodCount> bankBloodCounts = bloodBankBloodCountRepository.findAllByBloodBank(user);
        return ResponseEntity.ok(bankBloodCounts);
    }

    @GetMapping("/bloodbank/count")
    public ResponseEntity<List<BloodBankBloodCount>> myCount(@RequestHeader("Authorization") String bearerToken) {
        String username = jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7));
        return count(username);
    }
}
