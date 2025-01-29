package com.imap143.realworld.user.model;

import lombok.Getter;

@Getter
public class UserSignUpRequest {
    private final String email;
    private final String username;
    private final String password;

    public UserSignUpRequest(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
