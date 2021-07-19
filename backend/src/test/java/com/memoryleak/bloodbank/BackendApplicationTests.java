package com.memoryleak.bloodbank;

import com.memoryleak.bloodbank.model.Location;
import com.memoryleak.bloodbank.model.User;
import com.memoryleak.bloodbank.notification.EmailNotificationHandler;
import com.memoryleak.bloodbank.repository.LocationRepository;
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

    @Autowired
    LocationRepository locationRepository;

    @Test
    void addUser() throws IOException {
        Location location = new Location();

        location.setLatitude(0);
        location.setLatitude(1.1);

        locationRepository.save(location);

        System.out.println("\n\n\n\n\nLOCATION: "+location.getId()+"\n\n\n\n\n");
    }



}
