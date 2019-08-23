package com.sm.ms.service;

import com.sm.ms.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    User findByPhoneNumber(String phoneNumber);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByPhoneNumber(String phoneNumber);

    User findByEmailIgnoreCase(String email);

    void save(User user);

    User findById(Long id);

    User getUserByAuth();
}
