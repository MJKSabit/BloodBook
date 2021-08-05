package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.model.*;
import com.memoryleak.bloodbank.notification.EmailNotification;
import com.memoryleak.bloodbank.repository.*;
import com.memoryleak.bloodbank.util.JwtTokenUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.regex.Pattern;

@Service
public class AuthService {

    public static final String JWT_KEY              = "jwt";
    public static final String BLOOD_GROUP_KEY      = "bloodGroup";
    public static final String NAME_KEY             = "name";
    public static final String IMAGE_URL_KEY        = "imageURL";
    public static final String ABOUT_KEY            = "about";
    public static final String ACTIVE_KEY           = "active";
    public static final String LAST_DONATION_KEY    = "lastDonation";
    public static final String USERNAME_KEY         = "username";
    public static final String PASSWORD_KEY         = "password";
    public static final String EMAIL_KEY            = "email";
    public static final String LATITUDE_KEY         = "latitude";
    public static final String LONGITUDE_KEY        = "longitude";

    public static final double DEFAULT_LATITUDE     = 0;
    public static final double DEFAULT_LONGITUDE    = 0;

    public static final String ROLE_USER            = "USER";
    public static final String ROLE_BLOOD_BANK      = "BLOODBANK";

    public static final String VERIFY_ACTIVATE      = "activate";

    private static final String[] BLOOD_GROUPS = {"A+", "B+", "AB+", "O+", "A-", "B-", "AB-", "O-"};

    public static final Pattern USERNAME_MATCHER = Pattern.compile(
            "^[A-Za-z]\\w{5,29}$"
    );
    public static final Pattern PASSWORD_MATCHER = Pattern.compile(
            "^.{8,20}$"
    );
    public static final Pattern EMAIL_MATCHER = Pattern.compile(
            "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}"
    );

    private final static Logger logger = LogManager.getLogger(AuthService.class);

    @Value("${BASE_URL:http://localhost:8080}")
    String BASE_URL;

    @Value("${FRONTEND_URL:http://localhost:3000}")
    String FRONTEND_URL;

    // Repositories

    @Autowired
    UserRepository userRepository;

    @Autowired
    GeneralUserRepository generalUserRepository;

    @Autowired
    BloodBankRepository bloodBankRepository;

    @Autowired
    BloodBankBloodCountRepository bloodBankBloodCountRepository;

    @Autowired
    LocationRepository locationRepository;

    // Services

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired @Qualifier("spring")
    EmailNotification emailNotification;

    // Utils

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public JSONObject authenticate(String username, String password) throws DisabledException, BadCredentialsException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        logger.info("Logged in User: {}", username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtTokenUtil.generateToken(userDetails);

        JSONObject data = new JSONObject();
        data.put(JWT_KEY, token);

        return data;
    }

    private boolean isValid(User user) {
        String username = user.getUsername(), email = user.getEmail(), password = user.getPassword();
        return USERNAME_MATCHER.matcher(username).matches() &&
                PASSWORD_MATCHER.matcher(password).matches() &&
                EMAIL_MATCHER.matcher(email).matches();
    }

    // De-Serializers

    private User retrieveUser(JSONObject signUpData) {
        User user = new User();
        user.setUsername(
                signUpData.getString(USERNAME_KEY));
        user.setPassword(
                signUpData.getString(PASSWORD_KEY));
        user.setEmail(
                signUpData.getString(EMAIL_KEY));
        user.setLocation(retrieveLocation(signUpData));

        return user;
    }

    private Location retrieveLocation(JSONObject signUpData) {
        Location location = new Location();
        location.setLongitude(
                signUpData.optDouble(LONGITUDE_KEY, DEFAULT_LONGITUDE));
        location.setLatitude(
                signUpData.optDouble(LATITUDE_KEY, DEFAULT_LATITUDE));

        return location;
    }

    private GeneralUser retrieveGeneralUser(JSONObject signUpData) {
        GeneralUser generalUser = new GeneralUser();
        generalUser.setBloodGroup(
                signUpData.getString(BLOOD_GROUP_KEY).toUpperCase());
        generalUser.setName(
                signUpData.getString(NAME_KEY));
        generalUser.setImageURL(
                signUpData.optString(IMAGE_URL_KEY));
        generalUser.setAbout(
                signUpData.getString(ABOUT_KEY));
        generalUser.setActiveDonor(
                signUpData.getBoolean(ACTIVE_KEY));
        generalUser.setLastDonation(new Date(
                signUpData.getLong(LAST_DONATION_KEY)));
        generalUser.setUser(
                retrieveUser(signUpData)
        );

        return generalUser;
    }

    private BloodBank retrieveBloodBank(JSONObject signUpData) {
        BloodBank bloodBank = new BloodBank();
        bloodBank.setName(
                signUpData.getString(NAME_KEY));
        bloodBank.setImageURL(
                signUpData.optString(IMAGE_URL_KEY));
        bloodBank.setAbout(
                signUpData.getString(ABOUT_KEY));
        bloodBank.setUser(
                retrieveUser(signUpData));
        return bloodBank;
    }


    // Password Encrypted Save

    private void saveWithRawPassword(GeneralUser user) throws ValidationException {

        if (!isValid(user.getUser()))
            throw new ValidationException();

        locationRepository.save(user.getUser().getLocation());
        userService.saveWithRawPassword(user.getUser(), ROLE_USER);
        generalUserRepository.save(user);
    }

    private void saveWithRawPassword(BloodBank user) throws ValidationException {

        if (!isValid(user.getUser()))
            throw new ValidationException();

        locationRepository.save(user.getUser().getLocation());
        userService.saveWithRawPassword(user.getUser(), ROLE_BLOOD_BANK);
        bloodBankRepository.save(user);
    }


    @Transactional
    public void registerNewUser(JSONObject signUpData) throws ValidationException {
        GeneralUser generalUser = retrieveGeneralUser(signUpData);
        User user = generalUser.getUser();

        if (userService.hasUser(user))
            throw new ValidationException();

        saveWithRawPassword(generalUser);

        String jwtVerification = jwtTokenUtil.generateVerifyToken(user.getUsername(), user.getEmail(), VERIFY_ACTIVATE);
        emailNotification.sendEmail(
                Collections.singletonList(user.getEmail()),
                "Confirm Your Account",
                "Go to the link below to activate your BloodBook Account\n"+
                        FRONTEND_URL+"/activate/"+jwtVerification
        );
    }

    @Transactional
    public void registerNewBloodBank(JSONObject signUpData) throws ValidationException {
        BloodBank bloodBank = retrieveBloodBank(signUpData);
        User user = bloodBank.getUser();

        if (userService.hasUser(user))
            throw new ValidationException();

        saveWithRawPassword(bloodBank);

        for (String bloodGroup : BLOOD_GROUPS)
            bloodBankBloodCountRepository.save(new BloodBankBloodCount(bloodBank, bloodGroup));

        emailNotification.sendEmail(
                Collections.singletonList(user.getEmail()),
                "Confirm Your Account",
                "Reply to this Email with Scanned Verified Document to activate BloodBank Account in BloodBook!"
        );
    }

    public boolean activateUser(JSONObject object) {
        String jwtVerify = object.getString(JWT_KEY);
        String username = jwtTokenUtil.validateAndGetUsernameFromToken(jwtVerify, VERIFY_ACTIVATE);

        if (username == null)
            return false;

        User user = userService.findByUsername(username);
        user.setActive(true);
        userService.update(user);

        return true;
    }

}
