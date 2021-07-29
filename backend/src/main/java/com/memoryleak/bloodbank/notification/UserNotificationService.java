package com.memoryleak.bloodbank.notification;

import com.github.messenger4j.Messenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserNotificationService {
    @Autowired
    @Qualifier("spring")
    EmailNotification emailNotification;

    @Autowired
    Messenger messenger;


}
