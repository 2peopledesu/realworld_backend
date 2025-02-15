package com.imap143.realworld.user.controller;

import com.imap143.realworld.exception.ResourceNotFoundException;
import com.imap143.realworld.security.CustomUserDetails;
import com.imap143.realworld.user.dto.ProfileResponseDto;
import com.imap143.realworld.user.service.ProfileService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.imap143.realworld.user.model.Profile;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/profiles/{username}")
    public ResponseEntity<ProfileResponseDto> getProfile(
            @PathVariable String username) {
        return profileService.viewProfile(username)
                .map(profile -> ResponseEntity.ok(new ProfileResponseDto(profile)))
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    @PutMapping("/profiles/{username}/follow")
    public ResponseEntity<ProfileResponseDto> followUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String username) {
        Profile profile = profileService.followUser(username, userDetails.getId());
        return ResponseEntity.ok(new ProfileResponseDto(profile));
    }

    @DeleteMapping("/profiles/{username}/follow")
    public ResponseEntity<ProfileResponseDto> unfollowUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String username) {
        Profile profile = profileService.unfollowUser(username, userDetails.getId());
        return ResponseEntity.ok(new ProfileResponseDto(profile));
    }
}
