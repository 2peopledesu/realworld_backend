package com.imap143.realworld.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"}),
    @UniqueConstraint(columnNames = {"username"})
})
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Getter
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Embedded
    private Password password;

    @Embedded
    private Profile profile;

    // It cannot be called externally, and encourage object creation through
    // the static method instead.
    protected User() {}

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
}
