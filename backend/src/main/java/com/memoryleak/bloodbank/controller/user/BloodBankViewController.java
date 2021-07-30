package com.memoryleak.bloodbank.controller.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.BloodBank;
import com.memoryleak.bloodbank.repository.BloodBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BloodBankViewController {

    @Autowired
    BloodBankRepository bloodBankRepository;

    @JsonView(View.Private.class)
    @GetMapping("/user/bloodbank/{bloodbank}")
    public ResponseEntity<BloodBank> getBloodBank(@PathVariable String bloodbank) {
        BloodBank bloodBank = bloodBankRepository.findGeneralUserByUserUsernameIgnoreCase(bloodbank);

        if (bloodbank == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(bloodBank);
    }
}
