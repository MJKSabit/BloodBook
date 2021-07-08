package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.model.User;

public interface UserService {

    void save(User user);

    User findByUsername(String username);

    User login(String username, String password);
}