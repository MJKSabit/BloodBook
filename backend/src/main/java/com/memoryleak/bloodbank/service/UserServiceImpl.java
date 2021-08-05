package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.model.User;
import com.memoryleak.bloodbank.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    public static final String USERNAME_KEY         = "username";
    public static final String PASSWORD_KEY         = "password";
    public static final String EMAIL_KEY            = "email";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user, String role) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsernameIgnoreCase(username);
    }

    @Override
    public User login(String username, String password) {
        return userRepository.findUserByUsernameIgnoreCase(username);
    }

    /**
     * Deserialize User
     */
    public User retrieveUser(User user, JSONObject data) {
        user.setUsername(
                data.getString(USERNAME_KEY));
        user.setPassword(
                data.getString(PASSWORD_KEY));
        user.setEmail(
                data.getString(EMAIL_KEY));

        return user;
    }

    public void saveWithRawPassword(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void saveWithRawPassword(User user, String roleUser) {
        user.setRole(roleUser);
        saveWithRawPassword(user);
    }

    public boolean matchPassword(User user, String password) {
        return bCryptPasswordEncoder.matches(password, user.getPassword());
    }

    public boolean hasUser(User user) {
        return userRepository.countUserByEmailOrUsernameIgnoreCase(user.getEmail(), user.getUsername()) > 0;
    }

    public void update(User user) {
        userRepository.save(user);
    }
}
