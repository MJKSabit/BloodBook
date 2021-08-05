package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.model.*;
import com.memoryleak.bloodbank.repository.GeneralUserRepository;
import com.memoryleak.bloodbank.util.JwtTokenUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Service
public class GeneralUserService {

    public static final String BLOOD_GROUP_KEY      = "bloodGroup";
    public static final String NAME_KEY             = "name";
    public static final String IMAGE_URL_KEY        = "imageURL";
    public static final String ABOUT_KEY            = "about";
    public static final String ACTIVE_KEY           = "active";
    public static final String LAST_DONATION_KEY    = "lastDonation";

    public static final String VERIFY_MESSENGER = "messenger";

    public static final int DONATION_GAP_DAYS = 56;

    @Autowired
    GeneralUserRepository generalUserRepository;

    @Autowired
    LocationService locationService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public GeneralUser get(String username) {
        return generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);
    }

    public GeneralUser getFromJWT(String jwt) {
        return get(jwtTokenUtil.getUsernameFromToken(jwt));
    }

    public GeneralUser retrieve(GeneralUser generalUser, JSONObject data) {
        generalUser.setBloodGroup(
                data.getString(BLOOD_GROUP_KEY).toUpperCase());
        generalUser.setName(
                data.getString(NAME_KEY));
        generalUser.setImageURL(
                data.optString(IMAGE_URL_KEY));
        generalUser.setAbout(
                data.getString(ABOUT_KEY));
        generalUser.setActiveDonor(
                data.getBoolean(ACTIVE_KEY));
        generalUser.setLastDonation(new Date(
                data.getLong(LAST_DONATION_KEY)));

        return generalUser;
    }

    public GeneralUser update(String jwt, JSONObject data) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt);
        GeneralUser generalUser = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);
        User user = generalUser.getUser();

        Location location = locationService.retrieveLocation(
                user.getLocation(), data
        );
        locationService.save(location);

        retrieve(generalUser, data);
        return generalUserRepository.save(generalUser);
    }

    public List<GeneralUser> getMatchingPost(Post post) {
        return generalUserRepository.getMatchPostRequirement(
                post.getBloodGroup(),
                Date.from(post.getNeeded().toInstant().minus(Duration.ofDays(DONATION_GAP_DAYS))),
                post.getLocation().getLatitude(),
                post.getLocation().getLongitude()
        );
    }

    public List<GeneralUser> getMatchingEvent(Event event) {
        return generalUserRepository.getMatchEventRequirement(
            event.getLocation().getLatitude(),
            event.getLocation().getLongitude());
    }

    public String generateMessengerToken(String jwt) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt);
        GeneralUser generalUser = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);
        User user = generalUser.getUser();
        return jwtTokenUtil.generateVerifyToken(
                username,
                user.getEmail(),
                VERIFY_MESSENGER
        );
    }

    public void deleteMessengerToken(String jwt) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt);
        GeneralUser generalUser = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);
        generalUser.setFacebook(null);
        generalUserRepository.save(generalUser);
    }

    public String saveMessengerToken(String jwt, String senderId) {
        String username = jwtTokenUtil.validateAndGetUsernameFromToken(jwt, VERIFY_MESSENGER);

        if ( username==null )
            return null;

        GeneralUser user = generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);
        user.setFacebook(senderId);
        generalUserRepository.save(user);
        return username;
    }

    public GeneralUser save(GeneralUser user) {
        return generalUserRepository.save(user);
    }

}
