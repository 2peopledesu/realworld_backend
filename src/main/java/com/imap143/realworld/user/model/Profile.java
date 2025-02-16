package com.imap143.realworld.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Embeddable
public class Profile {
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Setter
    @Column(name = "bio")
    private String bio;

    @Setter
    @Column(name = "image")
    private String image; // address

    @Transient
    private boolean following;

    public Profile(String username) {
        this(username, null, null);
    }

    protected Profile() {
    }

    private Profile(String username, String bio, String image) {
        this.username = username;
        this.bio = bio;
        this.image = image;
        this.following = false;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public void setUsername(String username) {
        if (username != null && !username.isBlank()) {
            this.username = username;
        }
    }

    public static Profile of(String username, String bio, String image) {
        return new Profile(username, bio, image);
    }
}