package com.imap143.realworld.user.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.imap143.realworld.user.model.UserSignUpRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeName("user")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
@Value
public class UserSignUpRequestDto {
    @jakarta.validation.constraints.Email
    String email;
    @NotBlank
    String username;
    @NotBlank
    String password;

    public UserSignUpRequest signUpRequest() {
        return new UserSignUpRequest(
                email,
                username,
                password
        );
    }
}
