package com.imap143.realworld.user.service;

import com.imap143.realworld.user.model.Password;
import com.imap143.realworld.user.model.User;
import com.imap143.realworld.user.model.UserSignUpRequest;
import com.imap143.realworld.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(UserSignUpRequest userSignUpRequest) {
        final var encodedPassword = Password.of(userSignUpRequest.getPassword(), passwordEncoder);
        return userRepository.save(User.of(
                userSignUpRequest.getEmail(), 
                userSignUpRequest.getUsername(),
                encodedPassword));
    }

    @Transactional(readOnly = true)
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.matchPassword(password, passwordEncoder));
    }
}
