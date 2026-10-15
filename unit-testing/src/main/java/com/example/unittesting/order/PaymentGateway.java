package com.example.unittesting.order;

/** Takes money from the customer. No real implementation here: use a mock in the tests. */
public interface PaymentGateway {

    /** Returns true if the payment worked, false if the bank said no. */
    boolean charge(String customerId, int amountInCents);

    void refund(String customerId, int amountInCents);
}
