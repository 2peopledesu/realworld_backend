package com.imap143.realworld.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Embeddable
@NoArgsConstructor
public class Profile {
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Setter
    @Column(name = "bio")
    private String bio;

    @Setter
    @Column(name = "image")
    private String image; // address

    private boolean following;

    public Profile(String username) {
        this(username, null, null, false);
    }

    private Profile(String username, String bio, String image, boolean following) {
        this.username = username;
        this.bio = bio;
        this.image = image;
        this.following = following;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getImage() {
        return image;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setUsername(String username) {
        if (username != null && !username.isBlank()) {
            this.username = username;
        }
    }

}
