package com.example.unittesting.calculator;

/**
 * LEVEL 1 - a very small class. Every method is easy to test.
 *
 * This code is FINISHED and correct. Do not change it.
 * Your job is to write the tests: see CalculatorTest.
 */
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    /** Divides a by b. Throws IllegalArgumentException("Cannot divide by zero") when b is 0. */
    public double divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return (double) a / b;
    }

    public boolean isEven(int number) {
        return number % 2 == 0;
    }

    /** Zero is NOT positive. */
    public boolean isPositive(int number) {
        return number > 0;
    }
}
