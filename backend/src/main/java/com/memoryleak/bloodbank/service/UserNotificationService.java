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

    private String getMapLink(double lat, double lng) {
        return "https://www.google.com/maps/search/?api=1&query="+lat+","+lng;
    }

    private String getPostLink(Post post) {
        return FRONTEND_URL+"/user/post/"+post.getId();
    }

    private String getEventLink(Event event) { return  FRONTEND_URL+"/user/event/"+event.getId(); }

    public void notifyUsers(Post post, List<GeneralUser> users) {
        List<String> emails = new ArrayList<>();
        List<String> facebookRecipients = new ArrayList<>();

        for (GeneralUser user : users) {
            if (user.equals(post.getUser()))
                continue;
            emails.add(user.getUser().getEmail());
            if (user.getFacebook() != null)
                facebookRecipients.add(user.getFacebook());

            // TODO : REMOVE AFTER REFACTOR
            postForUserRepository.save(new GeneralUserToPost(user, post));
        }

        String subject = String.format("Request for %s Blood nearby you", post.getBloodGroup());
        String postText = String.format("Hello BloodBook User,\n" +
                "%s (%s) has requested for '%s' blood\n" +
                "Blood needed at: %s (Requested at: %s)\n" +
                "Information:\n" +
                "%s\n\n" +
                "Location: %s\n" +
                "Link: %s\n" +
                 " - BloodBook",
                post.getUser().getName(), post.getUser().getUser().getEmail(), post.getBloodGroup(),
                post.getNeeded().toString(), post.getPosted().toString(),
                post.getInfo(),
                getMapLink(post.getLocation().getLatitude(), post.getLocation().getLongitude()),
                getPostLink(post));

        sendEmail(emails, subject, postText);
        sendFacebookMessage(facebookRecipients, postText);
    }

    public void notifyUsers(Event event, List<GeneralUser> users) {
        List<String> emails = new ArrayList<>();
        List<String> facebookRecipients = new ArrayList<>();

        for (GeneralUser user : users) {
            emails.add(user.getUser().getEmail());
            if (user.getFacebook() != null)
                facebookRecipients.add(user.getFacebook());

            eventForUserRepository.save(new GeneralUserToEvent(user, event));
        }

        String subject = String.format("Upcoming Event by %s", event.getUser().getName());
        String postText = String.format("Hello BloodBook User,\n" +
                        "%s (%s) is going to arrange an event near you\n" +
                        "At: %s (Posted At: %s)\n" +
                        "Information:\n" +
                        "%s\n\n" +
                        "Location: %s\n" +
                        "Link: %s\n" +
                        " - BloodBook",
                event.getUser().getName(), event.getUser().getUser().getEmail(),
                event.getEventDate().toString(), event.getPosted().toString(),
                event.getInfo(),
                getMapLink(event.getLocation().getLatitude(), event.getLocation().getLongitude()),
                getEventLink(event));

        sendEmail(emails, subject, postText);
        sendFacebookMessage(facebookRecipients, postText);
    }

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
