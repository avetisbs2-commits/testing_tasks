package com.example.unittesting.bank;

/**
 * LEVEL 3 - an object with STATE. The result of a method depends on what happened before.
 * Tests must start with a fresh account every time, and must check that failed
 * operations do NOT change the balance.
 *
 * This code is FINISHED and correct. Do not change it.
 * Your job is to write the tests: see BankAccountTest.
 */
public class BankAccount {

    private final String owner;
    private int balance;

    /** Throws IllegalArgumentException if the start balance is negative. Zero is allowed. */
    public BankAccount(String owner, int initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.owner = owner;
        this.balance = initialBalance;
    }

    public String getOwner() {
        return owner;
    }

    public int getBalance() {
        return balance;
    }

    /** Adds money. Throws IllegalArgumentException("Amount must be positive") if amount is 0 or less. */
    public void deposit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        balance += amount;
    }

    /**
     * Takes money out.
     * Throws IllegalArgumentException("Amount must be positive") if amount is 0 or less.
     * Throws InsufficientFundsException("Insufficient funds") if amount is more than the balance.
     * Taking exactly the whole balance is allowed.
     */
    public void withdraw(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        balance -= amount;
    }

    /**
     * Moves money to another account.
     * Throws IllegalArgumentException("Target account is required") if the target is null.
     * If the withdraw fails, nothing changes in BOTH accounts.
     */
    public void transferTo(BankAccount target, int amount) {
        if (target == null) {
            throw new IllegalArgumentException("Target account is required");
        }
        withdraw(amount);
        target.deposit(amount);
    }
}
