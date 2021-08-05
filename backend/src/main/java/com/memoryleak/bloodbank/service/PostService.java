package com.memoryleak.bloodbank.service;

import com.memoryleak.bloodbank.controller.user.PostDetails;
import com.memoryleak.bloodbank.model.*;
import com.memoryleak.bloodbank.repository.BloodBankRepository;
import com.memoryleak.bloodbank.repository.PostForUserRepository;
import com.memoryleak.bloodbank.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PostService {
    public final static int PAGE_SIZE = 30;

    @Value("${FRONTEND_URL:https://blood-book.netlify.app}")
    String FRONTEND_URL;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostForUserRepository postForUserRepository;

    @Autowired
    LocationService locationService;

    @Autowired
    GeneralUserService generalUserService;

    @Autowired
    BloodBankRepository bloodBankRepository;

    @Autowired
    UserNotificationService notificationService;

    public Post get(long id) {
        return postRepository.findPostById(id);
    }

    public Slice<Post> postByUser(String username, int page) {
        return postRepository.findByUser(
                generalUserService.get(username),
                PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending())
        );
    }

    public Slice<Post> postForUser(String jwt, int page) {
        return postForUserRepository.findAllPostForUser(
                generalUserService.getFromJWT(jwt),
                PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending())
        );
    }

    public Slice<Post> allPosts(int page) {
        return postRepository.findAll(
                PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending())
        );
    }

    private List<BloodBank> bloodBanksForPost(Post post) {
        return bloodBankRepository.getBloodBankMatchingRequirements(
                post.getBloodGroup(),
                post.getLocation().getLatitude(),
                post.getLocation().getLongitude()
        );
    }

    public PostDetails getPostDetails(long id, String jwt) {
        GeneralUser user = generalUserService.getFromJWT(jwt);
        Post post = get(id);

        return user.equals(post.getUser()) ?
                new PostDetails(post, bloodBanksForPost(post)) :
                new PostDetails(post);
    }

    @Transactional
    public Post delete(long id, String jwt) throws IllegalAccessError {
        Post post = get(id);
        if (post == null) return null;

        GeneralUser user = generalUserService.getFromJWT(jwt);
        if (!post.getUser().equals(user)) throw new IllegalAccessError();

        // TODO : DELETE QUERY
        List<GeneralUserToPost> generalUserToPosts = postForUserRepository.findAllByPost(post);
        for (GeneralUserToPost generalUserToPost: generalUserToPosts)
            postForUserRepository.delete(generalUserToPost);

        postRepository.delete(post);
        return post;
    }

    @Transactional
    public Post create(String jwt, Post post, boolean notify) {
        GeneralUser user = generalUserService.getFromJWT(jwt);

        Location location = post.getLocation();
        location.setId(null);
        locationService.save(location);

        post.setId(null);
        post.setUser(user);
        post.setPosted(new Date());

        postRepository.save(post);

        // TODO : IN NEW THREAD
        postPersonalization(post, notify);

        return post;
    }

    public Post changeManagedStatus(String jwt, long id) throws IllegalAccessError {
        Post post = get(id);
        if (post == null) return null;

        if (!post.getUser().equals(generalUserService.getFromJWT(jwt)))
            throw new IllegalAccessError();

        post.setManaged(!post.isManaged());
        return postRepository.save(post);
    }

    private String getPostLink(Post post) {
        return FRONTEND_URL+"/user/post/"+post.getId();
    }

    private String getMapLink(double lat, double lng) {
        return "https://www.google.com/maps/search/?api=1&query="+lat+","+lng;
    }

    private String sendText(Post post) {
        return String.format("Hello BloodBook User,\n" +
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
            getPostLink(post)
        );
    }

    private String sendSubject(Post post) {
        return String.format("Request for %s Blood nearby you", post.getBloodGroup());
    }

    // TODO : ASYNC
    public void postPersonalization(Post post, boolean notify) {
        List<GeneralUser> users = generalUserService.getMatchingPost(post);

        List<String> emails = new ArrayList<>();
        List<String> messengerId = new ArrayList<>();

        for (GeneralUser user : users) {
            if (user.equals(post.getUser()))
                continue;

            if (notify) {
                emails.add(user.getUser().getEmail());
                if (user.getFacebook() != null)
                    messengerId.add(user.getFacebook());
            }

            postForUserRepository.save(new GeneralUserToPost(user, post));
        }

        if (notify) {
            String subjectText = sendSubject(post);
            String postText = sendText(post);
            notificationService.sendFacebookMessage(messengerId, postText);
            notificationService.sendEmail(emails, subjectText, postText);
        }
    }

}
