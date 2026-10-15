package com.example.unittesting.cart;

/** A product with a price in cents (so we never use decimal numbers for money). */
public record Product(String name, int priceInCents) {
}
