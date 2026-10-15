package com.example.unittesting.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * LEVEL 3 - OBJECTS WITH STATE.
 *
 * A BankAccount remembers its balance. So:
 *   - every test must start with a NEW account (tests must not depend on each other)
 *   - you must also test what does NOT change: after a failed withdraw, the balance is the same
 *   - test the BOUNDARIES: exactly the whole balance, and one more than the balance
 *
 * New tools:
 *   @BeforeEach          a method that runs before EVERY test (a fresh start)
 *   assertAll(...)       check several things, and see all the failures, not only the first
 *   assertThrows returns the exception, so you can check its message
 */
class BankAccountTest {

    private BankAccount account;

    @BeforeEach
    void setUp() {
        account = new BankAccount("Anna", 100);   // a NEW account with 100 before every test
    }

    // ---------------------------------------------------------------
    // WORKED EXAMPLES
    // ---------------------------------------------------------------
    @Test
    void deposit_positiveAmount_increasesTheBalance() {
        account.deposit(50);

        assertEquals(150, account.getBalance());
    }

    @Test
    void constructor_setsOwnerAndBalance() {
        BankAccount other = new BankAccount("Tom", 20);

        assertAll(
                () -> assertEquals("Tom", other.getOwner()),
                () -> assertEquals(20, other.getBalance())
        );
    }

    // ---------------------------------------------------------------
    // YOUR TESTS
    // ---------------------------------------------------------------

    // constructor
    // TODO 1: new BankAccount("Tom", -1)         throws IllegalArgumentException
    // TODO 2: new BankAccount("Tom", 0)          is allowed (balance is 0)   <- boundary

    // deposit
    // TODO 3: deposit(0)                         throws IllegalArgumentException   <- boundary
    // TODO 4: deposit(-5)                        throws IllegalArgumentException
    // TODO 5: after a failed deposit, the balance is still 100

    // withdraw
    // TODO 6: withdraw(30)                       the balance becomes 70
    // TODO 7: withdraw(100)                      the balance becomes 0          <- boundary: the WHOLE balance is allowed
    // TODO 8: withdraw(101)                      throws InsufficientFundsException  <- boundary: ONE more than the balance
    // TODO 9: ...and its message is "Insufficient funds"
    // TODO 10: after a failed withdraw(101), the balance is still 100
    // TODO 11: withdraw(0) and withdraw(-5)      throw IllegalArgumentException with the message "Amount must be positive"

    // transferTo
    // TODO 12: transferTo(other, 40)             this account has 60, the other account has 40 more
    // TODO 13: transferTo(null, 10)              throws IllegalArgumentException, message "Target account is required"
    // TODO 14: transferTo(other, 500)            throws InsufficientFundsException,
    //                                            and BOTH balances are unchanged (use assertAll)
}
