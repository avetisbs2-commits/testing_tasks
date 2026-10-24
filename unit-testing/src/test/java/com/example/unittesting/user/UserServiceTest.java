package com.example.unittesting.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    @Test
    void register_newUser_sendsWelcomeEmailOnceWithSavedUserData() {
        when(repository.findByEmail("anna@test.com")).thenReturn(Optional.empty());

        User savedByDatabase= new User(1L, "Anna", "anna@test.com", true);
        when(repository.save(any(User.class))).thenReturn(savedByDatabase);

        service.register("Anna", "anna@test.com");

        verify(emailSender).sendWelcome("anna@test.com", "Anna");
    }
    // TODO 2: a new user: the user given to repository.save has id null, the right name and email,
    //         and active = true.   (use an ArgumentCaptor)
    @Test
    void register_newUser_checksUserData() {
        when(repository.findByEmail("anna@test.com")).thenReturn(Optional.empty());

        User savedByDatabase= new User(1L, "Anna", "anna@test.com", true);
        when(repository.save(any(User.class))).thenReturn(savedByDatabase);

        service.register("Anna", "anna@test.com");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        User capturedUser = captor.getValue();
        assertNull(capturedUser.id());
        assertEquals("Anna", capturedUser.name());
        assertEquals("anna@test.com", capturedUser.email());
        assertTrue(capturedUser.active());
    }
    // TODO 3: the email is already registered (findByEmail returns Optional.of(someUser)):
    //         throws IllegalStateException with the message "Email already registered"
    // TODO 4: ... and in that case NOTHING is saved and NO email is sent (never, verifyNoInteractions)
    @Test
    void register_newUser_checksUserEmail() {

        User savedByDatabase= new User(1L, "Anna", "anna@test.com", true);

        when(repository.findByEmail("anna@test.com")).thenReturn(Optional.of(savedByDatabase));
        var e = assertThrows(IllegalStateException.class , () -> service.register("Anna", "anna@test.com"));

        assertEquals("Email already registered" , e.getMessage());
        verify(repository, never()).save(any(User.class));
        verifyNoInteractions(emailSender);

    }


    // TODO 5: a null name and a blank name ("   ") throw IllegalArgumentException "Name is required"
    //         (tip: @ParameterizedTest with @NullAndEmptySource and @ValueSource(strings = {"   "}))
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "}  )
    void register_blankName_throwsIllegalArgumentException(String text) {
        var e = assertThrows(IllegalArgumentException.class , () -> service.register(text, "anna@test.com"));
        assertEquals("Name is required" , e.getMessage());
    }


    // TODO 6: a null email, and an email without "@", throw IllegalArgumentException "Email is not valid"
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"annatest.com"} )
    void register_invalidEmail_throwsIllegalArgumentException(String text) {
        var e = assertThrows(IllegalArgumentException.class , () -> service.register("Anna", text));
        assertEquals("Email is not valid" , e.getMessage());
    }
    // TODO 7: when the name or the email is not valid, the repository is not used at all
    //         (verifyNoInteractions(repository))
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void register_invalidName_doesNotUseRepository(String name) {
        assertThrows(IllegalArgumentException.class, () -> service.register(name, "anna@test.com"));

        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"annatest.com"})
    void register_invalidEmail_doesNotUseRepository(String email) {
        assertThrows(IllegalArgumentException.class, () -> service.register("Anna", email));

        verifyNoInteractions(repository);
    }

    // ---------------------------------------------------------------
    // YOUR TESTS - deactivate
    // ---------------------------------------------------------------

    // TODO 8:  the user does not exist (findById returns Optional.empty()):
    //          throws NoSuchElementException with the message "User not found: 7"  (for id 7)
    @Test
    void deactivate_invalidUser_throwsNoSuchElementException() {
        when(repository.findById(7L)).thenReturn(Optional.empty());
        var e = assertThrows(NoSuchElementException.class , () -> service.deactivate(7L));

        assertEquals("User not found: 7", e.getMessage());
    }

    // TODO 9:  an active user: a COPY with active = false is saved (same id, name, email). Use a captor.
    @Test
    void deactivate_activeUser_setsActiveToFalse() {
        User existingUser = new User(7L, "Anna", "anna@test.com", true);

        when(repository.findById(7L)).thenReturn(Optional.of(existingUser));

        service.deactivate(7L);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(repository).save(captor.capture());

        User capturedUser = captor.getValue();
        assertFalse(capturedUser.active());
    }

    // TODO 10: an inactive user: nothing is saved (never)
    @Test
    void deactivate_inactiveUser_checksThatNotSaved() {
        User inactivUser = new User(7L, "Anna", "anna@test.com", false);

        when(repository.findById(7L)).thenReturn(Optional.of(inactivUser));

        service.deactivate(7L);

        verify(repository, never()).save(any(User.class));
    }
}
