package com.memoryleak.bloodbank.controller.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.BloodBank;
import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.Location;
import com.memoryleak.bloodbank.model.User;
import com.memoryleak.bloodbank.repository.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.memoryleak.bloodbank.controller.user.PostController.PAGE_SIZE;

@RestController
public class UserAccessController {

    @Autowired
    BloodBankRepository bloodBankRepository;

    @Autowired
    GeneralUserRepository generalUserRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LocationRepository locationRepository;

    @GetMapping("/admin/overview")
    public String getOverview() {
        JSONObject response = new JSONObject();

        response.put("users", generalUserRepository.count());
        response.put("banks", bloodBankRepository.count());
        response.put("posts", postRepository.count());
        response.put("events", eventRepository.count());

        return response.toString();
    }

    @GetMapping("/admin/users")
    public ResponseEntity<Slice<User>> active(@RequestParam String role,
                                              @RequestParam boolean active,
                                              @RequestParam boolean banned,
                                              @RequestParam(required = false, defaultValue = "0") int page) {
        if (!(role.equals("USER") || role.equals("BLOODBANK")))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(
                userRepository.findUserByActiveAndBannedAndRole(
                        active, banned, role,
                        PageRequest.of(page, PAGE_SIZE)
                )
        );
    }

    private void updateUser(User updateTo, User updateWith) {
        Location location = updateTo.getLocation();
        location.setLatitude(updateWith.getLocation().getLatitude());
        location.setLongitude(updateWith.getLocation().getLongitude());
        locationRepository.save(location);

        updateTo.setEmail(updateWith.getEmail());
        updateTo.setUsername(updateWith.getUsername());
        userRepository.save(updateTo);
    }


    @PostMapping("/admin/user")
    public ResponseEntity<User> setStatus(@RequestBody String strReq) {
        JSONObject request = new JSONObject(strReq);
        String username = request.getString("username");
        boolean active = request.getBoolean("active");
        boolean banned = request.getBoolean("banned");

        User user = userRepository.findUserByUsernameIgnoreCase(username);

        if (user == null)
            return ResponseEntity.notFound().build();

        user.setActive(active);
        user.setBanned(banned);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    @Transactional
    @JsonView(View.Private.class)
    @PostMapping("admin/bloodbank/{username}")
    public ResponseEntity<BloodBank> setBloodBank(@PathVariable String username,
                                          @RequestBody BloodBank rBloodBank) {
        BloodBank bloodBank = bloodBankRepository.findBloodBankByUserUsernameIgnoreCase(username);
        if (bloodBank==null)
            return ResponseEntity.notFound().build();

        updateUser(bloodBank.getUser(), rBloodBank.getUser());

        bloodBank.setName(rBloodBank.getName());
        bloodBank.setImageURL(rBloodBank.getImageURL());
        bloodBank.setAbout(rBloodBank.getAbout());
        bloodBankRepository.save(bloodBank);

        return ResponseEntity.ok(bloodBank);
    }

    @JsonView(View.Private.class)
    @GetMapping("admin/bloodbank/{username}")
    public ResponseEntity<BloodBank> setBloodBank(@PathVariable String username) {
        BloodBank bloodBank = bloodBankRepository.findBloodBankByUserUsernameIgnoreCase(username);
        if (bloodBank==null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(bloodBank);
    }

    @Transactional
    @JsonView(View.Private.class)
    @PostMapping("admin/user/{username}")
    public ResponseEntity<GeneralUser> setUser(@PathVariable String username,
                                                    @RequestBody GeneralUser rGeneralUser) {
        GeneralUser generalUser = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);

        if (generalUser==null)
            return ResponseEntity.notFound().build();

        updateUser(generalUser.getUser(), rGeneralUser.getUser());

        generalUser.setName(rGeneralUser.getName());
        generalUser.setImageURL(rGeneralUser.getImageURL());
        generalUser.setAbout(rGeneralUser.getAbout());
        generalUser.setFacebook(rGeneralUser.getFacebook());
        generalUser.setActiveDonor(rGeneralUser.isActiveDonor());
        generalUser.setLastDonation(rGeneralUser.getLastDonation());
        generalUser.setBloodGroup(rGeneralUser.getBloodGroup());
        generalUserRepository.save(generalUser);

        return ResponseEntity.ok(generalUser);
    }

    @JsonView(View.Private.class)
    @GetMapping("admin/user/{username}")
    public ResponseEntity<GeneralUser> getUser(@PathVariable String username) {
        GeneralUser generalUser = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);

        if (generalUser==null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(generalUser);
    }
}
