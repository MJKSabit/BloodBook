package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.model.BloodBank;
import com.memoryleak.bloodbank.model.BloodBankBloodCount;
import com.memoryleak.bloodbank.model.Location;
import com.memoryleak.bloodbank.model.User;
import com.memoryleak.bloodbank.repository.BloodBankBloodCountRepository;
import com.memoryleak.bloodbank.repository.BloodBankRepository;
import com.memoryleak.bloodbank.util.JwtTokenUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloodBankService {

    public static final String NAME_KEY             = "name";
    public static final String IMAGE_URL_KEY        = "imageURL";
    public static final String ABOUT_KEY            = "about";

    public static final String[] BLOOD_GROUPS       = {"A+", "B+", "AB+", "O+", "A-", "B-", "AB-", "O-"};

    @Autowired
    BloodBankRepository bloodBankRepository;

    @Autowired
    BloodBankBloodCountRepository bloodBankBloodCountRepository;

    @Autowired
    UserService userService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public BloodBank get(String username) {
        return bloodBankRepository.findBloodBankByUserUsernameIgnoreCase(username);
    }

    public BloodBank getFromJWT(String jwt) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt);
        return get(username);
    }

    public List<BloodBankBloodCount> getBloodCount(String username) {
        BloodBank bloodBank = get(username);
        if (bloodBank == null)
            return null;
        return bloodBankBloodCountRepository.findAllByBloodBank(bloodBank, Sort.by("id").ascending());
    }

    public List<BloodBankBloodCount> getBloodCountFromJWT(String jwt) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt);
        return getBloodCount(username);
    }

    public void createBloodCounts(BloodBank bloodBank) {
        for (String bloodGroup : BLOOD_GROUPS)
            bloodBankBloodCountRepository.save(new BloodBankBloodCount(bloodBank, bloodGroup));
    }

    public List<BloodBankBloodCount> setBloodCount(String jwt, JSONObject counts) {
        List<BloodBankBloodCount> bankBloodCounts = getBloodCountFromJWT(jwt);

        for (BloodBankBloodCount bloodCount : bankBloodCounts) {
            int count = counts.optInt(bloodCount.getBloodGroup(), 0);
            bloodCount.setInStock(Math.max(count, 0));
            bloodBankBloodCountRepository.save(bloodCount);
        }

        return bankBloodCounts;
    }

    public BloodBank update(String jwt, JSONObject data) {
        BloodBank bloodBank = getFromJWT(jwt);

        bloodBank.setImageURL(
                data.optString(IMAGE_URL_KEY));
        bloodBank.setAbout(
                data.getString(ABOUT_KEY));

        return save(bloodBank);
    }

    public BloodBank retrieveBloodBank(BloodBank bloodBank, JSONObject data) {
        bloodBank.setName(
                data.getString(NAME_KEY));
        bloodBank.setImageURL(
                data.optString(IMAGE_URL_KEY));
        bloodBank.setAbout(
                data.getString(ABOUT_KEY));
        return bloodBank;
    }

    public BloodBank save(BloodBank bloodBank) {
        return bloodBankRepository.save(bloodBank);
    }

    public List<BloodBank> bloodBanksNearUser(String jwt) {
        User user = userService.findByUsername(jwtTokenUtil.getUsernameFromToken(jwt));
        Location location = user.getLocation();
        return bloodBankRepository.exploreNearbyBloodBank(
                location.getLatitude(), location.getLongitude()
        );
    }
}
