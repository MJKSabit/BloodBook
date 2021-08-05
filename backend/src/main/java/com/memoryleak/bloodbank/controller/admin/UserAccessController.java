package com.memoryleak.bloodbank.controller.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.BloodBank;
import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.User;
import com.memoryleak.bloodbank.service.AdminService;
import com.memoryleak.bloodbank.service.BloodBankService;
import com.memoryleak.bloodbank.service.GeneralUserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserAccessController {

    @Autowired
    AdminService adminService;

    @Autowired
    BloodBankService bloodBankService;

    @Autowired
    GeneralUserService generalUserService;

    @GetMapping("/admin/overview")
    public String getOverview() {
        return adminService.overview().toString();
    }

    @GetMapping("/admin/users")
    public ResponseEntity<Slice<User>> active(@RequestParam String role,
                                              @RequestParam boolean active,
                                              @RequestParam boolean banned,
                                              @RequestParam(required = false, defaultValue = "0") int page) {
        Slice<User> users = adminService.users(role, active, banned, page);
        if (users==null)
            return ResponseEntity.badRequest().build();
        else
            return ResponseEntity.ok(users);
    }

    @PostMapping("/admin/user")
    public ResponseEntity<User> setStatus(@RequestBody String strReq) {
        User user = adminService.accessControl(new JSONObject(strReq));
        if (user == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(user);
    }

    @JsonView(View.Private.class)
    @PostMapping("admin/bloodbank/{username}")
    public ResponseEntity<BloodBank> setBloodBank(@PathVariable String username,
                                          @RequestBody BloodBank rBloodBank) {
        BloodBank bloodBank = adminService.updateBloodBank(username, rBloodBank);
        if (bloodBank==null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(bloodBank);
    }

    @JsonView(View.Private.class)
    @GetMapping("admin/bloodbank/{username}")
    public ResponseEntity<BloodBank> getBloodBank(@PathVariable String username) {
        BloodBank bloodBank = bloodBankService.get(username);

        if (bloodBank==null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(bloodBank);
    }

    @JsonView(View.Private.class)
    @PostMapping("admin/user/{username}")
    public ResponseEntity<GeneralUser> setUser(@PathVariable String username,
                                                    @RequestBody GeneralUser rGeneralUser) {
        GeneralUser generalUser = adminService.updateGeneralUser(username, rGeneralUser);

        if (generalUser==null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(generalUser);
    }

    @JsonView(View.Private.class)
    @GetMapping("admin/user/{username}")
    public ResponseEntity<GeneralUser> getUser(@PathVariable String username) {
        GeneralUser generalUser = generalUserService.get(username);

        if (generalUser==null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(generalUser);
    }
}
