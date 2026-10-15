package com.example.unittesting.order;

/** Tells the customer that the order is done. No real implementation here: use a mock in the tests. */
public interface Notifier {

    void sendConfirmation(String customerId, int amountInCents);
}
