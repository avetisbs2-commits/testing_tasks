package com.example.unittesting.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LEVEL 5 - SCENARIOS.
 *
 * Now there is no list of the exact tests. You get a list of SITUATIONS and you decide
 * the numbers and the checks. A good test uses numbers where a wrong calculation gives a
 * DIFFERENT answer (do not use 1 and 1: 1 + 1 and 1 * 1 look different, but 2 + 2 and 2 * 2 do not!).
 *
 * Think about:
 *   - the state: what happens after several operations in a row?
 *   - the money: prices are in cents. The discount is rounded DOWN. Test a rounding case!
 *   - what must NOT change: a failed operation must leave the cart as it was
 */
class ShoppingCartTest {

    private ShoppingCart cart;

    // Some products to use in your tests (prices in cents):
    private final Product phone = new Product("Phone", 30000);
    private final Product phoneCase = new Product("Case", 1500);
    private final Product pen = new Product("Pen", 333);

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    // ---------------------------------------------------------------
    // WORKED EXAMPLE
    // ---------------------------------------------------------------
    @Test
    void newCart_isEmptyAndCostsNothing() {
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItemCount());
        assertEquals(0, cart.getTotalInCents());
    }

    // ---------------------------------------------------------------
    // YOUR TESTS - situations to cover
    // ---------------------------------------------------------------

    // Adding
    // TODO 1:  one product, quantity 1       -> the cart is not empty, total = its price
    // TODO 2:  one product, quantity 3       -> item count 3, total = price x 3
    // TODO 3:  two DIFFERENT products        -> item count and total are the sums
    // TODO 4:  the SAME product added twice  -> the quantity grows (item count and total show it)
    // TODO 5:  add(null, 1), add(phone, 0), add(phone, -1)  -> IllegalArgumentException each
    // TODO 6:  after a failed add, the cart is still empty

    // Removing
    // TODO 7:  remove a product that is in the cart  -> returns true, the total goes down
    // TODO 8:  remove a product that is NOT in the cart -> returns false, nothing changes
    // TODO 9:  remove from an empty cart -> returns false
    // TODO 10: remove the only product -> the cart is empty again

    // Discounts
    // TODO 11: "SAVE10" takes 10 percent off (use a total that divides well, like 30000)
    // TODO 12: "HALF" takes 50 percent off
    // TODO 13: ROUNDING: 3 pens = 999 cents, with "SAVE10" the total is 900 (the discount 99.9 is rounded DOWN to 99)
    // TODO 14: a discount on an empty cart -> total is 0
    // TODO 15: an unknown code (like "FREE") -> IllegalArgumentException, message "Unknown discount code"
    // TODO 16: a null code -> IllegalArgumentException
    // TODO 17: codes do NOT add up: "SAVE10" and then "HALF" -> only 50 percent
    // TODO 18: a WRONG code does not remove the discount that is already set:
    //          apply "HALF", try "FREE" (it throws), the total is still half
    // TODO 19: the discount is used for products added AFTER the code, too
    // TODO 20: remove a product after the discount -> the total is recalculated with the discount
}
