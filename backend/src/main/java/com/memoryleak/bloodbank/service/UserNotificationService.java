package com.memoryleak.bloodbank.service;

import com.github.messenger4j.Messenger;
import com.github.messenger4j.exception.MessengerApiException;
import com.github.messenger4j.exception.MessengerIOException;
import com.github.messenger4j.send.MessagePayload;
import com.github.messenger4j.send.MessagingType;
import com.github.messenger4j.send.NotificationType;
import com.github.messenger4j.send.message.TextMessage;
import com.github.messenger4j.send.recipient.IdRecipient;
import com.memoryleak.bloodbank.model.*;
import com.memoryleak.bloodbank.repository.EventForUserRepository;
import com.memoryleak.bloodbank.repository.PostForUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Service
public class UserNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(UserNotificationService.class);

    @Value("${FRONTEND_URL:https://blood-book.netlify.app}")
    String FRONTEND_URL;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    PostForUserRepository postForUserRepository;

    @Autowired
    EventForUserRepository eventForUserRepository;

    @Autowired
    Messenger messenger;

    public void sendFacebookMessage(List<String> recipients, String message) {
        for(String recipientId: recipients) {
            try {
                final IdRecipient recipient = IdRecipient.create(recipientId);
                final NotificationType notificationType = NotificationType.REGULAR;
                final String metadata = "DEVELOPER_DEFINED_METADATA";

                final TextMessage textMessage = TextMessage.create(message, empty(), of(metadata));
                final MessagePayload messagePayload = MessagePayload.create(recipient, MessagingType.RESPONSE, textMessage,
                        of(notificationType), empty());
                this.messenger.send(messagePayload);
            } catch (MessengerApiException | MessengerIOException e) {
                logger.error(e.toString());
            }
        }
    }

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
