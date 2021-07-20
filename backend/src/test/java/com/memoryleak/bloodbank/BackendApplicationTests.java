package com.memoryleak.bloodbank;

import com.memoryleak.bloodbank.model.Location;
import com.memoryleak.bloodbank.notification.EmailNotificationSpring;
import com.memoryleak.bloodbank.repository.LocationRepository;
import com.memoryleak.bloodbank.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    EmailNotificationSpring emailNotificationSpring;

    @Test
    void addUser() throws IOException {
        List<String> recepient = new ArrayList<>();
//        emailNotificationSpring.sendEmail(recepient, "Test", "This is a test E-mail for all");
    }



}
