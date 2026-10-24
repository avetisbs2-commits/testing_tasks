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
    @Test
    void constructor_negativeBalance_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("Tom", -1));
    }
    // TODO 2: new BankAccount("Tom", 0)          is allowed (balance is 0)   <- boundary
    @Test
    void constructor_setsOwnerAndBalanceZero() {
        BankAccount other = new BankAccount("Tom", 0);

        assertAll(
                () -> assertEquals("Tom", other.getOwner()),
                () -> assertEquals(0, other.getBalance())
        );
    }

    // deposit
    // TODO 3: deposit(0)                         throws IllegalArgumentException   <- boundary
    @Test
    void deposit_zero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,() -> account.deposit(0));
    }
    // TODO 4: deposit(-5)                        throws IllegalArgumentException
    @Test
    void deposit_minus_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,() -> account.deposit(-5));
    }
    // TODO 5: after a failed deposit, the balance is still 100
    @Test
    void getBalance_shouldBeNotChanged(){
        assertEquals(100 , account.getBalance());
    }


    // withdraw
    // TODO 6: withdraw(30)                       the balance becomes 70
    @Test
    void getBalanceAfterWithdraw_returnChangedBalance(){
        account.withdraw(30);
        assertEquals(70 , account.getBalance());
    }
    // TODO 7: withdraw(100)                      the balance becomes 0          <- boundary: the WHOLE balance is allowed
    @Test
    void withdraw_zero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,() -> account.withdraw(0));
    }
    // TODO 8: withdraw(101)                      throws InsufficientFundsException  <- boundary: ONE more than the balance
    // TODO 9: ...and its message is "Insufficient funds"
    // TODO 10: after a failed withdraw(101), the balance is still 100
    @Test
    void withdraw_moreThanBalance_throwsException() {
        var e = assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(101)
        );
        assertEquals("Insufficient funds", e.getMessage());
        assertEquals(100, account.getBalance());
    }

    // TODO 11: withdraw(0) and withdraw(-5)      throw IllegalArgumentException with the message "Amount must be positive"
    @Test
    void withdraw_minus_throwsException() {
        var e = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(0)
        );
        var e2 = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(-5)
        );
        assertEquals("Amount must be positive", e.getMessage());
        assertEquals("Amount must be positive", e2.getMessage());
    }
    // transferTo
    // TODO 12: transferTo(other, 40)             this account has 60, the other account has 40 more
    @Test
    void transferTo_validAmount_updatesBothBalances() {
        BankAccount otherAccount = new BankAccount("Other", 0);

        account.transferTo(otherAccount, 40);

        assertAll(
                () -> assertEquals(60, account.getBalance()),
                () -> assertEquals(40, otherAccount.getBalance())
        );
    }
    // TODO 13: transferTo(null, 10)              throws IllegalArgumentException, message "Target account is required"
    @Test
    void transferTo_nullTarget_throwsIllegalArgumentException() {
        var e = assertThrows(
                IllegalArgumentException.class,
                () -> account.transferTo(null, 10)
        );

        assertEquals("Target account is required", e.getMessage());
    }
    // TODO 14: transferTo(other, 500)            throws InsufficientFundsException,
    //                                            and BOTH balances are unchanged (use assertAll)
    @Test
    void transferTo_insufficientFunds_throwsExceptionAndPreservesBothBalances() {
        int accountBalance = account.getBalance();
        BankAccount otherAccount = new BankAccount("Other", 50);
        int otherBalance = otherAccount.getBalance();

        assertThrows(
                InsufficientFundsException.class,
                () -> account.transferTo(otherAccount, 500)
        );

        assertAll(
                () -> assertEquals(accountBalance, account.getBalance()),
                () -> assertEquals(otherBalance, otherAccount.getBalance())
        );
    }
}
