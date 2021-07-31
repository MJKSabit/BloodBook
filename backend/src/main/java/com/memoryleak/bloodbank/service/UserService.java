package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.model.User;

public interface UserService {

    default void save(User user) {
        save(user, "USER");
    }

    void save(User user, String role);

    User findByUsername(String username);

    User login(String username, String password);
}