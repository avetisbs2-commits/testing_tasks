package com.example.unittesting.user;

import java.util.Optional;

/**
 * Saves and finds users in a database.
 * There is NO real implementation in this project. In the tests you use a MOCK (a fake).
 */
public interface UserRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findById(long id);

    /** Saves the user and returns the saved user (with an id). */
    User save(User user);
}
