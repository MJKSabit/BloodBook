package com.memoryleak.bloodbank;

import com.memoryleak.bloodbank.notification.EmailNotificationHandler;
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
    EmailNotificationHandler emailNotificationHandler;

    @Test
    void contextLoads() throws IOException {
        List<String> to = new ArrayList<>();

        String subject = "From Spring Boot-2";
        String body = "This is body!";

        System.out.println(emailNotificationHandler.sendEmail(to, subject, body));
    }

}
