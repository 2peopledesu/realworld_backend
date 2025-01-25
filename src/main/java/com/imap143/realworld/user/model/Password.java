package com.imap143.realworld.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.security.crypto.password.PasswordEncoder;

@Embeddable
public class Password {
    @Column(name = "password", nullable = false)
    private String encodedPassword;

    public static Password of(String password, PasswordEncoder passwordEncoder){
        return new Password(passwordEncoder.encode(password));
    }

    private Password(String encodedPassword){
        this.encodedPassword = encodedPassword;
    }

    protected Password() {

    }
}
