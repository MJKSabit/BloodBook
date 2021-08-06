package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.model.*;
import com.memoryleak.bloodbank.util.JwtTokenUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.regex.Pattern;

@Service
public class AuthService {

    public static final String JWT_KEY              = "jwt";
    public static final String OLD_KEY              = "old";
    public static final String NEW_KEY              = "new";

    public static final String USERNAME_KEY         = "username";
    public static final String PASSWORD_KEY         = "password";

    public static final String ROLE_USER            = "USER";
    public static final String ROLE_BLOOD_BANK      = "BLOODBANK";
    public static final String ROLE_ADMIN           = "ADMIN";

    public static final String VERIFY_ACTIVATE      = "activate";
    public static final String VERIFY_FORGOT        = "forgot";


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

    // Services

    @Autowired
    UserService userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    LocationService locationService;

    @Autowired
    GeneralUserService generalUserService;

    @Autowired
    BloodBankService bloodBankService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserNotificationService notificationService;

    // Utils

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public JSONObject authenticate(JSONObject loginData) throws DisabledException, BadCredentialsException {
        String username = loginData.getString(USERNAME_KEY);
        String password = loginData.getString(PASSWORD_KEY);
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

    private User retrieveUser(JSONObject data) {
        User user = new User();
        user.setLocation(retrieveLocation(data));
        return userService.retrieveUser(user, data);
    }

    private Location retrieveLocation(JSONObject data) {
        return locationService.retrieveLocation(
                new Location(), data
        );
    }

    private GeneralUser retrieveGeneralUser(JSONObject data) {
        GeneralUser generalUser = new GeneralUser();
        generalUser.setUser(retrieveUser(data));
        return generalUserService.retrieve(generalUser, data);
    }

    private BloodBank retrieveBloodBank(JSONObject signUpData) {
        BloodBank bloodBank = new BloodBank();
        bloodBank.setUser(retrieveUser(signUpData));
        return bloodBankService.retrieveBloodBank(bloodBank, signUpData);
    }


    // Password Encrypted Save

    private void saveWithRawPassword(GeneralUser user) throws ValidationException {

        if (!isValid(user.getUser()))
            throw new ValidationException();

        locationService.save(user.getUser().getLocation());
        userService.saveWithRawPassword(user.getUser(), ROLE_USER);
        generalUserService.save(user);
    }

    private void saveWithRawPassword(BloodBank user) throws ValidationException {

        if (!isValid(user.getUser()))
            throw new ValidationException();

        locationService.save(user.getUser().getLocation());
        userService.saveWithRawPassword(user.getUser(), ROLE_BLOOD_BANK);
        bloodBankService.save(user);
    }


    @Transactional
    public void registerNewUser(JSONObject signUpData) throws ValidationException {
        GeneralUser generalUser = retrieveGeneralUser(signUpData);
        User user = generalUser.getUser();

        if (userService.hasUser(user))
            throw new ValidationException();

        saveWithRawPassword(generalUser);

        String jwtVerification = jwtTokenUtil.generateVerifyToken(user.getUsername(), user.getEmail(), VERIFY_ACTIVATE);
        notificationService.sendEmail(
                Collections.singletonList(user.getEmail()),
                "Confirm Your Account",
                "Go to the link below to activate your BloodBook Account \n" +
                        "[Sorry for <"+FRONTEND_URL+">, I was blocked and site was flagged not safe]\n"+
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
        bloodBankService.createBloodCounts(bloodBank);

        notificationService.sendEmail(
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

    public boolean changePassword(String jwt, JSONObject request) {
        String oldPassword = request.getString(OLD_KEY);
        String newPassword = request.getString(NEW_KEY);

        if (!PASSWORD_MATCHER.matcher(newPassword).matches())
            return false;

        String username = jwtTokenUtil.getUsernameFromToken(jwt);
        User user = userService.findByUsername(username);

        if (userService.matchPassword(user, oldPassword)) {
            user.setPassword(newPassword);
            userService.saveWithRawPassword(user);
            return true;
        } else {
            return false;
        }
    }

    public boolean forgotPassword(JSONObject data) {
        User user = userService.findByUsername(data.getString(USERNAME_KEY));

        if (user == null)
            return false;

        String jwtVerification = jwtTokenUtil.generateVerifyToken(
                user.getUsername(), user.getEmail(), VERIFY_FORGOT);

        notificationService.sendEmail(
                Collections.singletonList(user.getEmail()),
                "Confirm Password Reset",
                "Go to the link below to reset your BloodBook Account Password\n"+
                        "[Sorry for <"+FRONTEND_URL+">, I was blocked and site was flagged not safe]\n"+
                        FRONTEND_URL+"/forgot/"+jwtVerification
        );

        return true;
    }

    public boolean resetPassword(JSONObject object) {
        String jwtVerify = object.getString(JWT_KEY);
        String password = object.getString(PASSWORD_KEY);
        String username = jwtTokenUtil.validateAndGetUsernameFromToken(jwtVerify, VERIFY_FORGOT);

        if (username == null)
            return false;

        User user = userService.findByUsername(username);
        user.setPassword(password);
        userService.saveWithRawPassword(user);
        return true;
    }

}
