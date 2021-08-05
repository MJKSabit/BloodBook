package com.memoryleak.bloodbank.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.memoryleak.bloodbank.config.View;
import com.memoryleak.bloodbank.controller.data.PostDetails;
import com.memoryleak.bloodbank.model.Post;
import com.memoryleak.bloodbank.service.PostService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostController {
    private final static Logger logger = LogManager.getLogger(PostController.class);

    @Autowired
    PostService postService;

    @JsonView(View.Public.class)
    @GetMapping("/user/posts/{username}")
    public Slice<Post> postsOfUser(@PathVariable String username, @RequestParam(required = false, defaultValue = "0") int page) {
        return postService.postByUser(username, page);
    }

    @JsonView(View.Public.class)
    @GetMapping("/user/posts")
    public Slice<Post> postsForUser(@RequestHeader("Authorization") String bearerToken,
                                    @RequestParam(required = false, defaultValue = "my", name = "for") String filter,
                                    @RequestParam(required = false, defaultValue = "0") int page) {

        switch (filter) {
            case "my":
                String jwt = bearerToken.substring(7);
                return postService.postForUser(jwt, page);
            case "all":
                return postService.allPosts(page);
            default:
                return null;
        }
    }

    @JsonView(View.Public.class)
    @GetMapping("/user/post/{id}")
    public ResponseEntity<PostDetails> getPost(@RequestHeader("Authorization") String bearerToken, @PathVariable long id) {
        String jwt = bearerToken.substring(7);
        PostDetails postDetails = postService.getPostDetails(id, jwt);
        return ResponseEntity.ok(postDetails);
    }

    @JsonView(View.Public.class)
    @DeleteMapping("/user/post/{id}")
    public ResponseEntity<Post> deletePost(@RequestHeader("Authorization") String bearerToken, @PathVariable long id) {
        String jwt = bearerToken.substring(7);
        try {
            Post post = postService.delete(id, jwt);

            if (post==null)
                return ResponseEntity.notFound().build();
            else
                return ResponseEntity.ok(post);

        } catch (IllegalAccessError ignore) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Transactional
    @JsonView(View.Public.class)
    @PostMapping("/user/post")
    public ResponseEntity<Post> createPost(@RequestHeader("Authorization") String bearerToken,
                                           @RequestBody Post post,
                                           @RequestParam(required = false, defaultValue = "true") boolean notify) {

        String jwt = bearerToken.substring(7);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                postService.create(jwt, post, notify)
        );
    }

    @JsonView(View.Public.class)
    @PostMapping("/user/post/{id}/managed")
    public ResponseEntity<Post> changeManaged(@RequestHeader("Authorization") String bearerToken, @PathVariable long id) {
        String jwt = bearerToken.substring(7);
        try {
            Post post = postService.changeManagedStatus(jwt, id);
            if (post == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(post);
        } catch (IllegalAccessError ignore) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
