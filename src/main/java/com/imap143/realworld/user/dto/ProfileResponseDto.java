package com.imap143.realworld.user.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.imap143.realworld.user.model.Profile;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@Getter
@JsonTypeName("profile")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
public class ProfileResponseDto {
    private final String username;
    private final String bio;
    private final String image;
    private final boolean following;

    public ProfileResponseDto(Profile profile) {
        this.username = profile.getUsername();
        this.bio = profile.getBio();
        this.image = profile.getImage();
        this.following = profile.isFollowing();
    }
} 