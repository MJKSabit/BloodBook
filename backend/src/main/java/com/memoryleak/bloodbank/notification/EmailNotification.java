package com.memoryleak.bloodbank.notification;

import java.io.IOException;
import java.util.List;

public interface EmailNotification {
    boolean sendEmail(List<String> to, String subject, String body) throws IOException;
}
