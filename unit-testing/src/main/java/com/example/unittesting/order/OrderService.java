package com.example.unittesting.order;

/**
 * LEVEL 7 - the BOSS. Three helpers (mocks), many paths, a boundary, and a rollback.
 * Order of the calls matters. Some calls must NEVER happen on some paths.
 *
 * This code is FINISHED and correct. Do not change it.
 * Your job is to write the tests: see OrderServiceTest.
 */
public class OrderService {

    /** An order of 100.00 (10000 cents) or more gets 10 percent off. */
    public static final int BIG_ORDER_LIMIT = 10_000;

    private final Inventory inventory;
    private final PaymentGateway payment;
    private final Notifier notifier;

    public OrderService(Inventory inventory, PaymentGateway payment, Notifier notifier) {
        this.inventory = inventory;
        this.payment = payment;
        this.notifier = notifier;
    }

    /**
     * The steps, in this order:
     *   1. quantity 0 or less      -> throws IllegalArgumentException("Quantity must be positive")
     *   2. not in stock            -> returns OUT_OF_STOCK  (no payment, no reserve, no message)
     *   3. total = price x quantity; if total >= BIG_ORDER_LIMIT, the total gets 10 percent off
     *      (the discount is rounded down: total - total / 10)
     *   4. payment is declined     -> returns PAYMENT_DECLINED  (nothing reserved, no message)
     *   5. reserve the items; if reserve throws a RuntimeException
     *                              -> the money is REFUNDED, returns FAILED  (no message)
     *   6. send the confirmation with the final total, returns SUCCESS
     */
    public OrderResult placeOrder(String customerId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (!inventory.isAvailable(productId, quantity)) {
            return OrderResult.OUT_OF_STOCK;
        }

        int total = inventory.getPriceInCents(productId) * quantity;
        if (total >= BIG_ORDER_LIMIT) {
            total = total - total / 10;
        }

        if (!payment.charge(customerId, total)) {
            return OrderResult.PAYMENT_DECLINED;
        }

        try {
            inventory.reserve(productId, quantity);
        } catch (RuntimeException e) {
            payment.refund(customerId, total);
            return OrderResult.FAILED;
        }

        notifier.sendConfirmation(customerId, total);
        return OrderResult.SUCCESS;
    }
}
