package com.workintech.twitter.service.impl;

import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.ResourceNotFoundException;
import com.workintech.twitter.exception.UnauthorizedException;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TweetServiceImplTest {

    private TweetRepository tweetRepository;
    private UserRepository userRepository;
    private TweetServiceImpl tweetService;

    @BeforeEach
    void setUp() {
        tweetRepository = mock(TweetRepository.class);
        userRepository = mock(UserRepository.class);
        tweetService = new TweetServiceImpl(tweetRepository, userRepository);
    }

    @Test
    void createTweet_Success() {
        User user = new User();
        user.setId(1L);
        Tweet tweet = new Tweet();
        tweet.setContent("Hello World");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tweetRepository.save(any(Tweet.class))).thenAnswer(i -> i.getArguments()[0]);

        Tweet created = tweetService.createTweet(tweet, 1L);

        assertEquals("Hello World", created.getContent());
        assertEquals(user, created.getUser());
        verify(tweetRepository, times(1)).save(tweet);
    }

    @Test
    void createTweet_UserNotFound() {
        Tweet tweet = new Tweet();
        tweet.setContent("Hello");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tweetService.createTweet(tweet, 1L));
        verify(tweetRepository, never()).save(any());
    }

    @Test
    void getTweetsByUserId_ReturnsList() {
        Tweet tweet1 = new Tweet();
        Tweet tweet2 = new Tweet();
        when(tweetRepository.findByUserId(1L)).thenReturn(List.of(tweet1, tweet2));

        List<Tweet> tweets = tweetService.getTweetsByUserId(1L);

        assertEquals(2, tweets.size());
        verify(tweetRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getTweetById_Found() {
        Tweet tweet = new Tweet();
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        Tweet found = tweetService.getTweetById(1L);

        assertEquals(tweet, found);
    }

    @Test
    void getTweetById_NotFound() {
        when(tweetRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tweetService.getTweetById(1L));
    }

    @Test
    void updateTweet_Success() {
        User user = new User();
        user.setId(1L);
        Tweet existing = new Tweet();
        existing.setId(1L);
        existing.setUser(user);
        existing.setContent("Old content");

        Tweet update = new Tweet();
        update.setContent("New content");

        when(tweetRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tweetRepository.save(any(Tweet.class))).thenAnswer(i -> i.getArguments()[0]);

        Tweet updated = tweetService.updateTweet(1L, update, 1L);

        assertEquals("New content", updated.getContent());
    }

    @Test
    void updateTweet_Unauthorized() {
        User user = new User();
        user.setId(1L);
        User otherUser = new User();
        otherUser.setId(2L);

        Tweet existing = new Tweet();
        existing.setId(1L);
        existing.setUser(user);

        Tweet update = new Tweet();
        update.setContent("New content");

        when(tweetRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(UnauthorizedException.class, () -> tweetService.updateTweet(1L, update, 2L));
    }

    @Test
    void updateTweet_NotFound() {
        Tweet update = new Tweet();
        update.setContent("New content");

        when(tweetRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tweetService.updateTweet(1L, update, 1L));
    }

    @Test
    void deleteTweet_Success() {
        User user = new User();
        user.setId(1L);
        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUser(user);

        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        tweetService.deleteTweet(1L, 1L);

        verify(tweetRepository, times(1)).delete(tweet);
    }

    @Test
    void deleteTweet_Unauthorized() {
        User user = new User();
        user.setId(1L);
        User otherUser = new User();
        otherUser.setId(2L);

        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUser(user);

        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        assertThrows(UnauthorizedException.class, () -> tweetService.deleteTweet(1L, 2L));
        verify(tweetRepository, never()).delete(any());
    }

    @Test
    void deleteTweet_NotFound() {
        when(tweetRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tweetService.deleteTweet(1L, 1L));
    }
}
