package com.imap143.realworld.user.service;
import com.imap143.realworld.user.model.User;
import com.imap143.realworld.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import com.imap143.realworld.user.model.Profile;
import com.imap143.realworld.exception.RealWorldException;

@Service
@Transactional
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Profile followUser(String username, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RealWorldException("User not found"));

        User userToFollow = userRepository.findByProfile_Username(username)
                .orElseThrow(() -> new RealWorldException("User to follow not found"));

        if (currentUser.getId() == userToFollow.getId()) {
            throw new RealWorldException("You cannot follow yourself");
        }

        currentUser.follow(userToFollow);
        userRepository.save(currentUser);
        userRepository.save(userToFollow);

        Profile profile = userToFollow.getProfile();
        profile.setFollowing(true);
        return profile;
    }

    public Profile unfollowUser(String username, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RealWorldException("User not found"));

        User userToUnfollow = userRepository.findByProfile_Username(username)
                .orElseThrow(() -> new RealWorldException("User to unfollow not found"));

        currentUser.unfollow(userToUnfollow);
        userRepository.save(currentUser);
        userRepository.save(userToUnfollow);

        Profile profile = userToUnfollow.getProfile();
        profile.setFollowing(false);
        return profile;
    }

    @Transactional(readOnly = true)
    public Optional<Profile> viewProfile(String username) {
        return userRepository.findByProfile_Username(username)
                .map(User::getProfile);
    }
}
    