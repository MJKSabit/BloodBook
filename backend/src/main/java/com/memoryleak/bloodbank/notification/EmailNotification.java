package com.memoryleak.bloodbank.notification;

import java.io.IOException;
import java.util.List;

public interface EmailNotification {
    void sendEmail(List<String> to, String subject, String body);
}
