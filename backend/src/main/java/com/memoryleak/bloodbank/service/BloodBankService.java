package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.model.BloodBank;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class BloodBankService {

    public static final String NAME_KEY             = "name";
    public static final String IMAGE_URL_KEY        = "imageURL";
    public static final String ABOUT_KEY            = "about";


    public BloodBank retrieveBloodBank(BloodBank bloodBank, JSONObject data) {
        bloodBank.setName(
                data.getString(NAME_KEY));
        bloodBank.setImageURL(
                data.optString(IMAGE_URL_KEY));
        bloodBank.setAbout(
                data.getString(ABOUT_KEY));
        return bloodBank;
    }
}
