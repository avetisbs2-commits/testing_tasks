package com.example.unittesting.password;

import java.util.ArrayList;
import java.util.List;

/**
 * LEVEL 4 - many rules, many combinations. One test per input is too much writing:
 * this is the place to use @ParameterizedTest. Think about BOUNDARIES (7 or 8 characters?).
 *
 * This code is FINISHED and correct. Do not change it.
 * Your job is to write the tests: see PasswordValidatorTest.
 */
public class PasswordValidator {

    public static final int MIN_LENGTH = 8;

    /**
     * Returns the list of problems. An empty list means the password is good.
     * The messages are exactly:
     *   "Password is required"                (null - and then nothing else is checked)
     *   "At least 8 characters"
     *   "At least one uppercase letter"
     *   "At least one lowercase letter"
     *   "At least one digit"
     *   "At least one special character"      (any character that is not a letter or a digit)
     * The problems are in this order.
     */
    public List<String> validate(String password) {
        List<String> problems = new ArrayList<>();

        if (password == null) {
            problems.add("Password is required");
            return problems;
        }

        if (password.length() < MIN_LENGTH) {
            problems.add("At least 8 characters");
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }

        if (!hasUpper) {
            problems.add("At least one uppercase letter");
        }
        if (!hasLower) {
            problems.add("At least one lowercase letter");
        }
        if (!hasDigit) {
            problems.add("At least one digit");
        }
        if (!hasSpecial) {
            problems.add("At least one special character");
        }
        return problems;
    }

    public boolean isValid(String password) {
        return validate(password).isEmpty();
    }
}
