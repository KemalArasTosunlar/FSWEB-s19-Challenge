package com.workintech.twitter.service.impl;

import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.ResourceNotFoundException;
import com.workintech.twitter.exception.UnauthorizedException;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import com.workintech.twitter.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    @Autowired
    public TweetServiceImpl(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Tweet createTweet(Tweet tweet, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        tweet.setUser(user);
        return tweetRepository.save(tweet);
    }

    @Override
    public List<Tweet> getTweetsByUserId(Long userId) {
        return tweetRepository.findByUserId(userId);
    }

    @Override
    public Tweet getTweetById(Long id) {
        return tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + id));
    }

    @Override
    public Tweet updateTweet(Long id, Tweet tweetDetails, Long userId) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + id));

        if (!tweet.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this tweet");
        }

        if (tweetDetails.getContent() != null && !tweetDetails.getContent().isBlank()) {
            tweet.setContent(tweetDetails.getContent());
        }

        return tweetRepository.save(tweet);
    }

    @Override
    public void deleteTweet(Long id, Long userId) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + id));

        if (!tweet.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this tweet");
        }

        tweetRepository.delete(tweet);
    }
}
