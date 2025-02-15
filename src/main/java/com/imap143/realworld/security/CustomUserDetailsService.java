package com.imap143.realworld.security;

import com.imap143.realworld.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            return userRepository.findById(Long.parseLong(userId))
                    .map(user -> new CustomUserDetails(user.getId(), user.getEmail(), user.getUsername()))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user id format: " + userId);
        }
    }
} 