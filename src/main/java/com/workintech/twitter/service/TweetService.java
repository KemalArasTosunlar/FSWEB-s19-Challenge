package com.workintech.twitter.service;

import com.workintech.twitter.entity.Tweet;

import java.util.List;

public interface TweetService {
    Tweet createTweet(Tweet tweet, Long userId);
    List<Tweet> getTweetsByUserId(Long userId);
    Tweet getTweetById(Long id);
    Tweet updateTweet(Long id, Tweet tweet, Long userId);
    void deleteTweet(Long id, Long userId);
}
