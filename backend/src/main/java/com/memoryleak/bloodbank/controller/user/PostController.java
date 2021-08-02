package com.memoryleak.bloodbank.controller.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.model.GeneralUser;
import com.memoryleak.bloodbank.model.Location;
import com.memoryleak.bloodbank.model.Post;
import com.memoryleak.bloodbank.notification.PostNotificationService;
import com.memoryleak.bloodbank.repository.*;
import com.memoryleak.bloodbank.util.JwtTokenUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@RestController
public class PostController {
    private final static Logger logger = LogManager.getLogger(PostController.class);

    public final static int PAGE_SIZE = 30;

    @Autowired
    PostRepository postRepository;

    @Autowired
    GeneralUserRepository generalUserRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    PostForUserRepository postForUserRepository;

    @Autowired
    BloodBankRepository bloodBankRepository;

    @Autowired
    PostNotificationService postNotificationService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;


    private GeneralUser getUser(String bearerToken) {
        String username = jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7));
        return generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username);
    }

    @JsonView(View.Public.class)
    @GetMapping("/user/posts/{username}")
    public Slice<Post> postsOfUser(@PathVariable String username, @RequestParam(required = false, defaultValue = "0") int page) {
        return postRepository.findByUser(
                generalUserRepository.findGeneralUserByUserUsernameIgnoreCase(username),
                PageRequest.of(page, PAGE_SIZE, Sort.by("posted").descending())
        );
    }

    @JsonView(View.Public.class)
    @GetMapping("/user/posts")
    public Slice<Post> postsForUser(@RequestHeader("Authorization") String bearerToken,
                                    @RequestParam(required = false, defaultValue = "my", name = "for") String filter,
                                    @RequestParam(required = false, defaultValue = "0") int page) {

        switch (filter) {
            case "my":
                GeneralUser generalUser = getUser(bearerToken);
                return postForUserRepository.findAllPostForUser(generalUser, PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending()));
            case "all":
                return postRepository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending()));
            default:
                return null;
        }
    }

    @JsonView(View.Public.class)
    @GetMapping("/user/post/{id}")
    public ResponseEntity<PostDetails> getPost(@RequestHeader("Authorization") String bearerToken, @PathVariable long id) {
        GeneralUser user = getUser(bearerToken);
        Post post = postRepository.findPostById(id);
        if (post == null)
            return ResponseEntity.notFound().build();

        PostDetails postDetails =  user.equals(post.getUser()) ?
                new PostDetails(
                        post,
                        bloodBankRepository.getBloodBankMatchingRequirements(
                                post.getBloodGroup(),
                                post.getLocation().getLatitude(),
                                post.getLocation().getLongitude()
                        )
                ) :
                new PostDetails(post);

        return ResponseEntity.ok(postDetails);
    }

    @JsonView(View.Public.class)
    @DeleteMapping("/user/post/{id}")
    public ResponseEntity<Post> deletePost(@RequestHeader("Authorization") String bearerToken, @PathVariable long id) {
        Post post = postRepository.findPostById(id);

        if (post==null)
            return ResponseEntity.badRequest().build();

        if (!post.getUser().equals(getUser(bearerToken))) {
            postRepository.delete(post);
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @JsonView(View.Public.class)
    @PostMapping("/user/post")
    public ResponseEntity<Post> createPost(@RequestHeader("Authorization") String bearerToken,
                                           @RequestBody Post post,
                                           @RequestParam(required = false, defaultValue = "true") boolean notify) {

        GeneralUser user = getUser(bearerToken);

        if (post==null)
            return ResponseEntity.badRequest().build();

        post.setId(null);

        Location location = post.getLocation();
        location.setId(null);
        location = locationRepository.save(location);

        post.setLocation(location);
        post.setUser(user);
        post.setPosted(new Date());

        post = postRepository.save(post);
        if (notify) {
            List<GeneralUser> users = generalUserRepository.getMatchPostRequirement(
                    post.getBloodGroup(),
                    Date.from(post.getNeeded().toInstant().minus(Duration.ofDays(56))),
                    post.getLocation().getLatitude(),
                    post.getLocation().getLongitude());

            postNotificationService.notifyUsers(post, users);
        }


        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @JsonView(View.Public.class)
    @PostMapping("/user/post/{id}/managed")
    public ResponseEntity<Post> changeManaged(@RequestHeader("Authorization") String bearerToken, @PathVariable long id) {
        Post post = postRepository.findPostById(id);

        if (post==null)
            return ResponseEntity.badRequest().build();

        if (!post.getUser().equals(getUser(bearerToken))) {
            post.setManaged(!post.isManaged());
            postRepository.save(post);
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
