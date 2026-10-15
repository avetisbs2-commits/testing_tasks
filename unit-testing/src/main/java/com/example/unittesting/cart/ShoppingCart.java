package com.example.unittesting.cart;

import java.util.ArrayList;
import java.util.List;

/**
 * LEVEL 5 - state + a list + calculations + a rule that changes the result (discount).
 * You must think about many scenarios and about ROUNDING.
 *
 * This code is FINISHED and correct. Do not change it.
 * Your job is to write the tests: see ShoppingCartTest.
 */
public class ShoppingCart {

    private static class Line {
        private final Product product;
        private int quantity;

        private Line(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    private final List<Line> lines = new ArrayList<>();
    private int discountPercent = 0;

    /**
     * Adds a product. If a product with the SAME NAME is already in the cart,
     * the quantity grows (there is still only one line).
     * Throws IllegalArgumentException if the product is null or the quantity is 0 or less.
     */
    public void add(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        for (Line line : lines) {
            if (line.product.name().equals(product.name())) {
                line.quantity += quantity;
                return;
            }
        }
        lines.add(new Line(product, quantity));
    }

    /** Removes the whole line of that product. Returns true if it was in the cart, false if not. */
    public boolean remove(String productName) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).product.name().equals(productName)) {
                lines.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    /** The total number of items. 2 phones + 3 cases = 5. */
    public int getItemCount() {
        int count = 0;
        for (Line line : lines) {
            count += line.quantity;
        }
        return count;
    }

    /**
     * Known codes: "SAVE10" = 10 percent off, "HALF" = 50 percent off.
     * A new code replaces the old one. Throws IllegalArgumentException("Unknown discount code")
     * for any other code, and for null. A wrong code does NOT remove a discount that was already set.
     */
    public void applyDiscountCode(String code) {
        if ("SAVE10".equals(code)) {
            discountPercent = 10;
        } else if ("HALF".equals(code)) {
            discountPercent = 50;
        } else {
            throw new IllegalArgumentException("Unknown discount code");
        }
    }

    /**
     * Total in cents after the discount. The discount is rounded DOWN (whole cents):
     * 999 cents with SAVE10 -> discount 99 -> total 900.
     * An empty cart costs 0.
     */
    public int getTotalInCents() {
        int total = 0;
        for (Line line : lines) {
            total += line.product.priceInCents() * line.quantity;
        }
        int discount = total * discountPercent / 100;
        return total - discount;
    }
}
