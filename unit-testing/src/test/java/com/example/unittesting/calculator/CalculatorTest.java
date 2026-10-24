package com.example.unittesting.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LEVEL 1 - YOUR FIRST TESTS.
 *
 * The class is Calculator (src/main). It is finished. YOU write the tests here.
 *
 * Every test has 3 parts:   1. prepare   2. do the action   3. check the result
 *
 * Your toolbox:
 *   assertEquals(expected, actual)            the two values must be equal
 *   assertEquals(expected, actual, 0.0001)    the same for double (small difference allowed)
 *   assertTrue(condition) / assertFalse(condition)
 *   assertThrows(IllegalArgumentException.class, () -> calculator.divide(1, 0))
 *
 * Name your tests like:  method_situation_expectedResult
 */
class CalculatorTest {

    private final Calculator calculator = new Calculator();

    // ---------------------------------------------------------------
    // WORKED EXAMPLE - read it, then write the others in the same way
    // ---------------------------------------------------------------
    @Test
    void add_twoPositiveNumbers_returnsTheSum() {
        int result = calculator.add(2, 3);

        assertEquals(5, result);
    }

    // ---------------------------------------------------------------
    // YOUR TESTS. Write one @Test method for each line.
    // ---------------------------------------------------------------

    // TODO 1:  add(-2, -3)               returns -5
    @Test
    void add_twoNegativeNumbers_returnsTheSum() {
        int result = calculator.add(-2, -3);

        assertEquals(-5, result);
    }


    // TODO 2:  add(7, 0)                 returns 7
    @Test
    void add_positiveNumberAndZero_returnsTheSum() {
        int result = calculator.add(7, 0);

        assertEquals(7, result);
    }



    // TODO 3:  subtract(10, 4)           returns 6
    @Test
    void subtract_twoPositiveNumbers_returnsTheDifference() {
        int result = calculator.subtract(10, 4);

        assertEquals(6, result);
    }


    // TODO 4:  subtract(4, 10)           returns -6   (the result is negative)
    @Test
    void subtract_smallerMinusLarger_returnsNegativeDifference() {
        int result = calculator.subtract(4, 10);
        assertEquals(-6, result);
    }

    // TODO 5: multiply(6, 7) returns 42
    @Test
    void multiply_twoPositiveNumbers_returnsProduct() {
        int result = calculator.multiply(6, 7);

        assertEquals(42, result);
    }
    // TODO 6:  multiply(5, 0)            returns 0
    @Test
    void multiply_positiveNumberAndZero_returnsProduct() {
        int result = calculator.multiply(5, 0);

        assertEquals(0, result);
    }

    // TODO 7:  divide(6, 3)              returns 2.0
    @Test
    void divide_twoPositiveNumbers_returnsQuotient() {
        double result = calculator.divide(6, 3);

        assertEquals(2, result);
    }

    // TODO 8: divide(7, 2) returns 3.5 (a result with decimals)
    @Test
    void divide_numbersWithDecimalResult_returnsQuotient() {
        double result = calculator.divide(7, 2);
        assertEquals(3.5, result);
    }

    // TODO 9:  divide(5, 0)              throws IllegalArgumentException
    // TODO 10: divide(5, 0)              the exception message is "Cannot divide by zero"
    //          (tip: save the exception:  var e = assertThrows(...);  then  e.getMessage())
    @Test
    void divide_byZero_throwsIllegalArgumentException() {
        var e = assertThrows(IllegalArgumentException.class, () -> calculator.divide(5, 0));

        assertEquals("Cannot divide by zero", e.getMessage());
    }

    // TODO 11: isEven(4) is true,  isEven(7) is false,  isEven(-3) is false
    //          (3 tests, or 1 test with 3 asserts)
    @Test
    void isEven_shouldReturnCorrectResultForVariousNumbers() {
        assertAll(
                () -> assertTrue(calculator.isEven(4) ),
                () -> assertFalse(calculator.isEven(7)),
                () -> assertFalse(calculator.isEven(-3))
        );
    }

    // TODO 12: isPositive(5) is true,  isPositive(-5) is false
    @Test
    void isPositive_shouldReturnCorrectResultForVariousNumbers() {
        assertAll(
                () -> assertTrue(calculator.isPositive(5) ),
                () -> assertFalse(calculator.isPositive(-5))
        );
    }

    // TODO 13: isPositive(0) is false   <- the BOUNDARY: zero is the number between "positive" and "negative"
}
