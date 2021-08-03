package com.memoryleak.bloodbank.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("spring")
public class EmailNotificationSpring implements EmailNotification {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendEmail(List<String> to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("sabit.jehadul.karim@gmail.com");

        if (to.size()>0) {
            message.setTo(to.get(0));
            String[] bccList = new String[to.size()-1];
            for (int i = 1; i < to.size(); i++)
                bccList[i-1] = to.get(i);
            message.setBcc(bccList);

            emailSender.send(message);
        }
    }
}
