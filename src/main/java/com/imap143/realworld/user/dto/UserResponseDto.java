package com.imap143.realworld.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.imap143.realworld.user.model.User;
import lombok.Value;

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

@JsonTypeName("user")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
@Value
public class UserResponseDto {
    String email;
    String token;
    String username;
    String bio;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String image;

    public UserResponseDto(String email, String token, String username, String bio, String image) {
        this.email = email;
        this.token = token;
        this.username = username;
        this.bio = bio;
        this.image = image;
    }

    public static UserResponseDto fromUser(User user, String token) {
        return new UserResponseDto(user.getEmail(),
                token,
                user.getUsername(),
                user.getBio(),
                null);
    }
}
