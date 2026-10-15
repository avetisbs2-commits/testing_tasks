package com.example.unittesting.user;

/** A user. The id is null until the user is saved. */
public record User(Long id, String name, String email, boolean active) {
}
