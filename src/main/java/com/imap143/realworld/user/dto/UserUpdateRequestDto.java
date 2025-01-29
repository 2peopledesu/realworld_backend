package com.imap143.realworld.user.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.imap143.realworld.user.model.UserUpdateRequest;
import lombok.Value;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeName("user")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
@Value
public class UserUpdateRequestDto {
    String email;
    String username;
    String bio;
    String image;
    String password;

    public UserUpdateRequest toUpdateRequest() {
        return UserUpdateRequest.builder()
                .email(email)
                .username(username)
                .bio(bio)
                .image(image)
                .password(password)
                .build();
    }
}
