package com.imap143.realworld.user.service;

import com.imap143.realworld.user.model.Password;
import com.imap143.realworld.user.model.User;
import com.imap143.realworld.user.model.UserSignUpRequest;
import com.imap143.realworld.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.imap143.realworld.user.model.UserUpdateRequest;
import com.imap143.realworld.exception.ResourceNotFoundException;
import com.imap143.realworld.exception.DuplicateEmailException;
import com.imap143.realworld.exception.DuplicateUsernameException;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByProfile_Username(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User update(long userId, UserUpdateRequest updateRequest) {
        User user = findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!updateRequest.hasChanges()) {
            return user;
        }

        updateRequest.getEmail().ifPresent(email -> 
            checkEmailDuplication(email, userId));
        updateRequest.getUsername().ifPresent(username -> 
            checkUsernameDuplication(username, userId));
        
        user.update(updateRequest, passwordEncoder);
        return user;
    }

    private void checkEmailDuplication(String email, long userId) {
        userRepository.findByEmail(email)
                .filter(user -> user.getId() != userId)
                .ifPresent(user -> {
                    throw new DuplicateEmailException("Email is already in use");
                });
    }

    private void checkUsernameDuplication(String username, long userId) {
        userRepository.findByProfile_Username(username)
                .filter(user -> user.getId() != userId)
                .ifPresent(user -> {
                    throw new DuplicateUsernameException("Username is already in use");
                });
    }
}
