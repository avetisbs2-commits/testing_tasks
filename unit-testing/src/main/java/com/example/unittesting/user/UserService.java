package com.example.unittesting.user;

import java.util.NoSuchElementException;

/**
 * LEVEL 6 - a class that USES other objects (a repository and an email sender).
 * You do not want a real database or real emails in a unit test, so you use MOCKS
 * (Mockito) and you check HOW the service talked to them: verify(...).
 *
 * This code is FINISHED and correct. Do not change it.
 * Your job is to write the tests: see UserServiceTest.
 */
public class UserService {

    private final UserRepository repository;
    private final EmailSender emailSender;

    public UserService(UserRepository repository, EmailSender emailSender) {
        this.repository = repository;
        this.emailSender = emailSender;
    }

    /**
     * Creates a new active user, saves it, and sends a welcome email. Returns the saved user.
     * Throws IllegalArgumentException("Name is required") if the name is null or blank.
     * Throws IllegalArgumentException("Email is not valid") if the email is null or has no "@".
     * Throws IllegalStateException("Email already registered") if a user with this email exists.
     * When it throws, nothing is saved and no email is sent.
     */
    public User register(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email is not valid");
        }
        if (repository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email already registered");
        }

        User saved = repository.save(new User(null, name, email, true));
        emailSender.sendWelcome(saved.email(), saved.name());
        return saved;
    }

    /**
     * Makes a user inactive (saves a copy with active = false).
     * Throws NoSuchElementException("User not found: <id>") if there is no such user.
     * If the user is already inactive, nothing is saved.
     */
    public void deactivate(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
        if (!user.active()) {
            return;
        }
        repository.save(new User(user.id(), user.name(), user.email(), false));
    }
}
