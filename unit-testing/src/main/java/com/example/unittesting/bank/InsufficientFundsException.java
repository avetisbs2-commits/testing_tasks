package com.example.unittesting.bank;

/** Thrown when somebody wants to take more money than the account has. */
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String message) {
        super(message);
    }
}
