package com.imap143.realworld.user.repository;

import com.imap143.realworld.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByProfile_Username(String username);

    /*
    Optional<User> findByEmailAndPassword(String email, String password);
    
    @Query("SELECT u FROM User u WHERE u.profile.userName = :userName AND u.password = :password")
    Optional<User> findByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);
    */
}