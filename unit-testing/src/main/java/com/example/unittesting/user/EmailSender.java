package com.example.unittesting.user;

/**
 * Sends emails. There is NO real implementation in this project.
 * In the tests you use a MOCK, so no real email is sent.
 */
public interface EmailSender {

    void sendWelcome(String email, String name);
}
