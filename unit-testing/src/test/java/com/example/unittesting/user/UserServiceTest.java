package com.example.unittesting.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * LEVEL 6 - MOCKS.
 *
 * UserService needs a UserRepository (a database) and an EmailSender. A UNIT test must not use a
 * real database or send real emails. So we use MOCKS: fake objects that you control.
 *
 * A mock does 2 things for you:
 *   1. STUBBING: you say what it answers.
 *        when(repository.findByEmail("a@b.com")).thenReturn(Optional.empty());
 *        when(repository.save(any(User.class))).thenAnswer(call -> call.getArgument(0));   // returns what it got
 *   2. VERIFYING: you check how it was used.
 *        verify(emailSender).sendWelcome("a@b.com", "Anna");     // called once with these values
 *        verify(repository, never()).save(any());                // never called
 *        verifyNoInteractions(emailSender);                      // nothing was called at all
 *
 * ArgumentCaptor catches the object that was given to a mock, so you can check its fields:
 *        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
 *        verify(repository).save(captor.capture());
 *        User saved = captor.getValue();
 *
 * Remember: a mock returns "empty" answers by default (null, 0, false, an empty Optional).
 */
class UserServiceTest {

    private UserRepository repository;
    private EmailSender emailSender;
    private UserService service;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        emailSender = mock(EmailSender.class);
        service = new UserService(repository, emailSender);
    }

    // ---------------------------------------------------------------
    // WORKED EXAMPLE
    // ---------------------------------------------------------------
    @Test
    void register_newUser_returnsTheSavedUser() {
        when(repository.findByEmail("anna@test.com")).thenReturn(Optional.empty());
        User savedByDatabase = new User(1L, "Anna", "anna@test.com", true);
        when(repository.save(any(User.class))).thenReturn(savedByDatabase);

        User result = service.register("Anna", "anna@test.com");

        assertEquals(savedByDatabase, result);
    }

    // ---------------------------------------------------------------
    // YOUR TESTS - register
    // ---------------------------------------------------------------

    // TODO 1: a new user: the welcome email is sent ONE time, with the email and the name of the SAVED user.
    //         (verify(emailSender).sendWelcome(...))
    // TODO 2: a new user: the user given to repository.save has id null, the right name and email,
    //         and active = true.   (use an ArgumentCaptor)
    // TODO 3: the email is already registered (findByEmail returns Optional.of(someUser)):
    //         throws IllegalStateException with the message "Email already registered"
    // TODO 4: ... and in that case NOTHING is saved and NO email is sent (never, verifyNoInteractions)
    // TODO 5: a null name and a blank name ("   ") throw IllegalArgumentException "Name is required"
    //         (tip: @ParameterizedTest with @NullAndEmptySource and @ValueSource(strings = {"   "}))
    // TODO 6: a null email, and an email without "@", throw IllegalArgumentException "Email is not valid"
    // TODO 7: when the name or the email is not valid, the repository is not used at all
    //         (verifyNoInteractions(repository))

    // ---------------------------------------------------------------
    // YOUR TESTS - deactivate
    // ---------------------------------------------------------------

    // TODO 8:  the user does not exist (findById returns Optional.empty()):
    //          throws NoSuchElementException with the message "User not found: 7"  (for id 7)
    // TODO 9:  an active user: a COPY with active = false is saved (same id, name, email). Use a captor.
    // TODO 10: an inactive user: nothing is saved (never)
}
