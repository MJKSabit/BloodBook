package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.model.User;
import com.memoryleak.bloodbank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

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

    public void saveWithRawPassword(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void saveWithRawPassword(User user, String roleUser) {
        user.setRole(roleUser);
        saveWithRawPassword(user);
    }

    public boolean hasUser(User user) {
        return userRepository.countUserByEmailOrUsernameIgnoreCase(user.getEmail(), user.getUsername()) > 0;
    }

    public void update(User user) {
        userRepository.save(user);
    }
}
