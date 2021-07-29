package com.memoryleak.bloodbank.notification;

import com.github.messenger4j.Messenger;
import com.github.messenger4j.exception.MessengerApiException;
import com.github.messenger4j.exception.MessengerIOException;
import com.github.messenger4j.send.MessagePayload;
import com.github.messenger4j.send.MessagingType;
import com.github.messenger4j.send.NotificationType;
import com.github.messenger4j.send.message.TextMessage;
import com.github.messenger4j.send.recipient.IdRecipient;
import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.GeneralUserToPost;
import com.memoryleak.bloodbank.model.Post;
import com.memoryleak.bloodbank.repository.PostForUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Service
public class PostNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(PostNotificationService.class);

    @Value("${FRONTEND_URL:https://blood-book.netlify.app}")
    String FRONTEND_URL;

    @Autowired
    @Qualifier("spring")
    EmailNotification emailNotification;

    @Autowired
    PostForUserRepository postForUserRepository;

    @Autowired
    Messenger messenger;

    private String getMapLink(double lat, double lng) {
        return "https://www.google.com/maps/search/?api=1&query="+lat+","+lng;
    }

    private String getPostLink(Post post) {
        return FRONTEND_URL+"/user/post/"+post.getId();
    }

    public void notifyUsers(Post post, List<GeneralUser> users) {
        List<String> emails = new ArrayList<>();
        List<String> facebookRecipients = new ArrayList<>();

        for (GeneralUser user : users) {
            if (user.equals(post.getUser()))
                continue;
            emails.add(user.getUser().getEmail());
            if (user.getFacebook() != null)
                facebookRecipients.add(user.getFacebook());

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
                post.getUser().getUser().getUsername(), post.getUser().getUser().getEmail(), post.getBloodGroup(),
                post.getNeeded().toString(), post.getPosted().toString(),
                post.getInfo(),
                getMapLink(post.getLocation().getLatitude(), post.getLocation().getLongitude()),
                getPostLink(post));

        emailNotification.sendEmail(emails, subject, postText);
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
}
