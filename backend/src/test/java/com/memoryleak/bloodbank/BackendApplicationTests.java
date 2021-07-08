package com.memoryleak.bloodbank;

import com.memoryleak.bloodbank.model.User;
import com.memoryleak.bloodbank.notification.EmailNotificationHandler;
import com.memoryleak.bloodbank.service.UserService;
import com.memoryleak.bloodbank.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    UserServiceImpl userService;

    @Test
    void addUser() throws IOException {
        User user = new User();
        user.setUsername("MJKSabit");
        user.setEmail("sabit.jehadul.karim@gmail.com");
        user.setPassword("password");
        userService.save(user);
    }



}
