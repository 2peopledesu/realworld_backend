package com.imap143.realworld.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", 
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}),
        @UniqueConstraint(columnNames = {"username"})
    },
    indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_username", columnList = "username")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Embedded
    private Password password;

    @Embedded
    private Profile profile;

    @ManyToMany
    @JoinTable(
        name = "user_follows",
        joinColumns = @JoinColumn(name = "follower_id"),
        inverseJoinColumns = @JoinColumn(name = "followed_id"),
        foreignKey = @ForeignKey(name = "fk_user_follower"),
        inverseForeignKey = @ForeignKey(name = "fk_user_followed")
    )
    private final Set<User> following = new HashSet<>();

    @ManyToMany(mappedBy = "following")
    private final Set<User> followers = new HashSet<>();

    // Existing user login
    public static User of(String email, String username, Password password) {
        return new User(email, new Profile(username), password);
    }

    private User(String email, Profile profile, Password password) {
        this.email = email;
        this.profile = profile;
        this.password = password;
    }

    public String getUsername() {
        return profile.getUsername();
    }

    public String getBio() {
        return profile.getBio();
    }

    public boolean matchPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return password.matches(rawPassword, passwordEncoder);
    }

    public void update(UserUpdateRequest request, PasswordEncoder passwordEncoder) {
        request.getEmail().ifPresent(this::updateEmail);
        request.getUsername().ifPresent(this::updateUsername);
        request.getBio().ifPresent(this::updateBio);
        request.getImage().ifPresent(this::updateImage);
        request.getPassword().ifPresent(password -> 
            this.password = Password.of(password, passwordEncoder));
    }

    private void updateEmail(String email) {
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
    }

    private void updateUsername(String username) {
        if (username != null && !username.isBlank()) {
            this.profile.setUsername(username);
        }
    }

    private void updateBio(String bio) {
        this.profile.setBio(bio);
    }

    private void updateImage(String image) {
        this.profile.setImage(image);
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public Set<User> getFollowing() {
        return following;
    }
}

