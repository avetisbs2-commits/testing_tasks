package com.example.unittesting.password;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LEVEL 4 - MANY RULES, MANY INPUTS.
 *
 * There are 5 rules and many ways to break them. Do not write 30 copies of the same test:
 * use @ParameterizedTest. Think like a tester:
 *   - test EACH rule alone (a password that breaks only that one rule)
 *   - test the BOUNDARY of the length rule (7 characters / 8 characters)
 *   - test null and empty
 *   - test a password that breaks SEVERAL rules: is the list complete and in the right order?
 *
 * New tools:
 *   @ValueSource(strings = {"a", "b"})     many inputs, one parameter
 *   @NullAndEmptySource                    runs the test with null and with ""
 *   @CsvSource                             many inputs and the expected answer
 *   List.of("a", "b")                      a list to compare with:  assertEquals(List.of(...), result)
 */
class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    // ---------------------------------------------------------------
    // WORKED EXAMPLES
    // ---------------------------------------------------------------
    @Test
    void validate_goodPassword_returnsNoProblems() {
        List<String> problems = validator.validate("Abcdef1!");

        assertTrue(problems.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "abcdefg1!, At least one uppercase letter",
            "ABCDEFG1!, At least one lowercase letter"
    })
    void validate_breakingOneLetterRule_returnsOnlyThatProblem(String password, String expectedProblem) {
        assertEquals(List.of(expectedProblem), validator.validate(password));
    }

    // ---------------------------------------------------------------
    // YOUR TESTS
    // ---------------------------------------------------------------

    // Each rule alone (the password breaks ONLY that rule):
    // TODO 1: a password with no digit              -> exactly ["At least one digit"]
    // TODO 2: a password with no special character  -> exactly ["At least one special character"]
    // TODO 3: a short password that is good in every other way, like "Ab1!"
    //                                               -> exactly ["At least 8 characters"]

    // The length boundary:
    // TODO 4: a good password of exactly 7 characters is NOT valid
    // TODO 5: a good password of exactly 8 characters IS valid     <- the boundary!

    // null and empty:
    // TODO 6: validate(null)                        -> exactly ["Password is required"]  (nothing else)
    // TODO 7: validate("")  - what problems does the empty text have? Write all of them, in order.
    //         (tip: run the test with a wrong guess, then READ the failure message)

    // Many problems at once:
    // TODO 8: validate("abc")                       -> 4 problems. Check the whole list and its ORDER.

    // Special characters:
    // TODO 9: a space counts as a special character. Prove it with a password that has a space and no other special character.
    // TODO 10: a @ParameterizedTest with @ValueSource: several different special characters (! @ # $ % - _ ?) are all accepted.

    // isValid:
    // TODO 11: isValid("Abcdef1!") is true
    // TODO 12: isValid("abc") is false
    // TODO 13: isValid(null) is false
}
