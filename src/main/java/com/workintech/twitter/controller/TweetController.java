package com.workintech.twitter.controller;

import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tweet")
public class TweetController {

    private final TweetService tweetService;

    @Autowired
    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Assuming the principal contains user id as Long or a custom UserDetails with getId()
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // If you have a custom UserDetails implementation with getId(), cast and get id
            // For now, assuming username is userId string, parse it
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            return Long.parseLong(username);
        } else if (principal instanceof String) {
            // principal is username string, parse as userId
            return Long.parseLong((String) principal);
        }
        throw new RuntimeException("Unable to get authenticated user id");
    }

    @PostMapping
    public ResponseEntity<Tweet> createTweet(@Valid @RequestBody Tweet tweet) {
        Long userId = getAuthenticatedUserId();
        Tweet createdTweet = tweetService.createTweet(tweet, userId);
        return ResponseEntity.ok(createdTweet);
    }

    @GetMapping("/findByUserId")
    public ResponseEntity<List<Tweet>> getTweetsByUserId(@RequestParam Long userId) {
        List<Tweet> tweets = tweetService.getTweetsByUserId(userId);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/findById")
    public ResponseEntity<Tweet> getTweetById(@RequestParam Long id) {
        Tweet tweet = tweetService.getTweetById(id);
        return ResponseEntity.ok(tweet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tweet> updateTweet(@PathVariable Long id, @Valid @RequestBody Tweet tweet) {
        Long userId = getAuthenticatedUserId();
        Tweet updatedTweet = tweetService.updateTweet(id, tweet, userId);
        return ResponseEntity.ok(updatedTweet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable Long id) {
        Long userId = getAuthenticatedUserId();
        tweetService.deleteTweet(id, userId);
        return ResponseEntity.noContent().build();
    }
}
