package com.imap143.realworld.user.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.imap143.realworld.user.model.User;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

/*
{
  "user": {
    "email": "jake@jake.jake",
    "token": "jwt.token.here",
    "username": "jake",
    "bio": "I work at State Farm.",
    "image": null
  }
}
 */

@Getter
@JsonTypeName("user")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
public class UserResponseDto {
    private final String email;
    private final String username;
    private final String bio;
    private final String image;
    private final String token;

    private UserResponseDto(String email, String username, String bio, String image, String token) {
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.image = image;
        this.token = token;
    }

    public static UserResponseDto fromUser(User user, String token) {
        return new UserResponseDto(
            user.getEmail(),
            user.getUsername(),
            user.getBio(),
            user.getProfile().getImage(),
            token
        );
    }
}
