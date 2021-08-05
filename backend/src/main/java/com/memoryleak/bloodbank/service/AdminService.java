package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.model.BloodBank;
import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.Location;
import com.memoryleak.bloodbank.model.User;
import com.memoryleak.bloodbank.repository.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    public final static int PAGE_SIZE = 30;

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

    public JSONObject overview() {
        JSONObject data = new JSONObject();
        data.put("Number of Users",         generalUserRepository.count());
        data.put("Number of BloodBanks",    bloodBankRepository.count());
        data.put("Number of Posts",         postRepository.count());
        data.put("Number of Events",        eventRepository.count());
        return data;
    }

    public Slice<User> users(String role, boolean active, boolean banned, int page) {
        if (!(role.equals(AuthService.ROLE_USER) || role.equals(AuthService.ROLE_BLOOD_BANK)))
            return null;
        return userRepository.findUserByActiveAndBannedAndRole(
                active, banned, role,
                PageRequest.of(page, PAGE_SIZE, Sort.by("id")));
    }

    public User accessControl(JSONObject data) {
        String username = data.getString("username");
        boolean active = data.getBoolean("active");
        boolean banned = data.getBoolean("banned");

        User user = userRepository.findUserByUsernameIgnoreCase(username);
        if (user == null) return null;

        user.setActive(active);
        user.setBanned(banned);
        userRepository.save(user);

        return user;
    }

    private void updateUser(User mergeTo, User mergeWith) {
        Location location = mergeTo.getLocation();
        location.setLatitude(mergeWith.getLocation().getLatitude());
        location.setLongitude(mergeWith.getLocation().getLongitude());
        locationRepository.save(location);

        mergeTo.setEmail(mergeWith.getEmail());
        mergeTo.setUsername(mergeWith.getUsername());
        userRepository.save(mergeTo);
    }

    public BloodBank updateBloodBank(BloodBank mergeTo, BloodBank mergeWith) {
        updateUser(mergeTo.getUser(), mergeWith.getUser());

        mergeTo.setName(mergeWith.getName());
        mergeTo.setImageURL(mergeWith.getImageURL());
        mergeTo.setAbout(mergeWith.getAbout());
        return bloodBankRepository.save(mergeTo);
    }

    public GeneralUser updateGeneralUser(GeneralUser mergeTo, GeneralUser mergeWith) {
        updateUser(mergeTo.getUser(), mergeWith.getUser());

        mergeTo.setName(mergeWith.getName());
        mergeTo.setImageURL(mergeWith.getImageURL());
        mergeTo.setAbout(mergeWith.getAbout());
        mergeTo.setFacebook(mergeWith.getFacebook());
        mergeTo.setActiveDonor(mergeWith.isActiveDonor());
        mergeTo.setLastDonation(mergeWith.getLastDonation());
        mergeTo.setBloodGroup(mergeWith.getBloodGroup());
        return generalUserRepository.save(mergeTo);
    }


}
