package com.imap143.realworld.user.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
public class UserUpdateRequest {
    private final Optional<String> email;
    private final Optional<String> username;
    private final Optional<String> bio;
    private final Optional<String> image;
    private final Optional<String> password;

    @Builder
    public UserUpdateRequest(String email, String username, String bio, String image, String password) {
        this.email = Optional.ofNullable(email);
        this.username = Optional.ofNullable(username);
        this.bio = Optional.ofNullable(bio);
        this.image = Optional.ofNullable(image);
        this.password = Optional.ofNullable(password);
    }

    public boolean hasChanges() {
        return email.isPresent() || username.isPresent() || bio.isPresent() 
                || image.isPresent() || password.isPresent();
    }
}
