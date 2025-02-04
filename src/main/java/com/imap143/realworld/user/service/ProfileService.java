package com.imap143.realworld.user.service;
import com.imap143.realworld.user.model.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import com.imap143.realworld.user.model.Profile;
import com.imap143.realworld.exception.ResourceNotFoundException;
import com.imap143.realworld.exception.RealWorldException;

@Service
@Transactional(readOnly = true)
public class ProfileService {

    private final UserService userService;

    public ProfileService(UserService userService) {
        this.userService = userService;
    }

    public Optional<Profile> viewProfile(String username) {
        return userService.findByUsername(username)
                .map(User::getProfile);
    }

    @Transactional
    public Profile followUser(String username, long currentUserId) {
        User currentUser = userService.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        User userToFollow = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User to follow not found"));

        if (currentUser.getId() != userToFollow.getId()) {
            Profile profile = userToFollow.getProfile();
            profile.setFollowing(true);
            return profile;
        }
        
        throw new RealWorldException("Users cannot follow themselves");
    }

    @Transactional
    public Profile unfollowUser(String username, long currentUserId) {
        User currentUser = userService.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        User userToUnfollow = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User to unfollow not found"));

        if (currentUser.getId() != userToUnfollow.getId()) {
            Profile profile = userToUnfollow.getProfile();
            profile.setFollowing(false);
            return profile;
        }
        
        throw new RealWorldException("Users cannot unfollow themselves");
    }
}
    