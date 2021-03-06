package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.model.Location;
import com.memoryleak.bloodbank.repository.LocationRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    public static final String LATITUDE_KEY         = "latitude";
    public static final String LONGITUDE_KEY        = "longitude";

    public static final double DEFAULT_LATITUDE     = 0;
    public static final double DEFAULT_LONGITUDE    = 0;

    public static final double MAX_LATITUDE         = 90;
    public static final double MAX_LONGITUDE        = 180;

    @Autowired
    LocationRepository locationRepository;

    /**
     * Deserializes Location
     * @param data  JSONObject
     * @return Deserialized Location
     */
    public Location retrieveLocation(Location location, JSONObject data) {
        location.setLongitude(
                data.optDouble(LONGITUDE_KEY, DEFAULT_LONGITUDE));
        location.setLatitude(
                data.optDouble(LATITUDE_KEY, DEFAULT_LATITUDE));

        return location;
    }

    public Location save(Location location) {
        if (Math.abs(location.getLongitude()) > MAX_LONGITUDE)
            location.setLongitude(DEFAULT_LONGITUDE);
        if (Math.abs(location.getLatitude()) > MAX_LATITUDE)
            location.setLongitude(DEFAULT_LATITUDE);

        return locationRepository.save(location);
    }
}
