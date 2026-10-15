package com.example.unittesting.order;

/** The warehouse. No real implementation here: use a mock in the tests. */
public interface Inventory {

    boolean isAvailable(String productId, int quantity);

    int getPriceInCents(String productId);

    /** Takes the items out of the warehouse. May throw a RuntimeException. */
    void reserve(String productId, int quantity);
}
