package com.example.unittesting.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    @Test
    void add_singleProductQuantity1_cartNotEmptyAndTotalMatchesPrice() {
        cart.add(phoneCase, 1);

        assertAll(
                () -> assertFalse(cart.isEmpty()),
                () -> assertEquals(1500, cart.getTotalInCents())
        );
    }
    // TODO 2:  one product, quantity 3       -> item count 3, total = price x 3
    @Test
    void add_singleProductQuantity3_cartNotEmptyAndTotalMatchesPrice() {
        cart.add(phoneCase, 3);

        assertAll(
                () -> assertFalse(cart.isEmpty()),
                () -> assertEquals(4500, cart.getTotalInCents() )
        );
    }
    // TODO 3:  two DIFFERENT products        -> item count and total are the sums
    @Test
    void add_twoProduct_cartNotEmptyAndTotalMatchesPrice() {
        cart.add(phoneCase, 1);
        cart.add(phone, 1);

        assertAll(
                () -> assertFalse(cart.isEmpty()),
                () -> assertEquals(2, cart.getItemCount()),
                () -> assertEquals(31500, cart.getTotalInCents() )
        );
    }

    // TODO 4: the SAME product added twice  -> the quantity grows (item count and total show it)
    @Test
    void add_sameProductTwice_quantityGrowsAndTotalUpdates() {
        cart.add(phoneCase, 1);
        assertAll(
                () -> assertEquals(1, cart.getItemCount()),
                () -> assertEquals(1500, cart.getTotalInCents())
        );

        cart.add(phoneCase, 1);
        assertAll(
                () -> assertEquals(2, cart.getItemCount()),
                () -> assertEquals(3000, cart.getTotalInCents())
        );
    }
    // TODO 5: add(null, 1), add(phone, 0), add(phone, -1) -> IllegalArgumentException each
    @Test
    void add_nullProduct_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> cart.add(null, 1)
        );
    }

    @Test
    void add_zeroQuantity_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> cart.add(phone, 0)
        );
    }

    @Test
    void add_negativeQuantity_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> cart.add(phone, -1)
        );
    }
    // TODO 6: after a failed add, the cart is still empty
    @Test
    void add_failedAdd_cartRemainsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> cart.add(null, 1));

        assertAll(() -> assertTrue(cart.isEmpty()),
                  () -> assertEquals(0, cart.getItemCount())
        );
    }
    // Removing
    // TODO 7: remove a product that is in the cart  -> returns true, the total goes down
    @Test
    void remove_productInCart_returnsTrueAndTotalDecreases() {
        cart.add(phone, 1);
        cart.add(phoneCase, 1);

        boolean isRemoved = cart.remove("Case");

        assertAll(
                () -> assertTrue(isRemoved),
                () -> assertEquals(30000, cart.getTotalInCents())
        );
    }
    // TODO 8:  remove a product that is NOT in the cart -> returns false, nothing changes
    // TODO 9:  remove from an empty cart -> returns false
    @Test
    void remove_productNotInCart_returnsTrueAndTotalDecreases() {

        boolean isRemoved = cart.remove("Case");

        assertAll(
                () -> assertFalse(isRemoved),
                () -> assertEquals(0, cart.getTotalInCents())
        );
    }
    // TODO 10: remove the only product -> the cart is empty again
    @Test
    void remove_onlyProductInCart_returnsTrueAndTotalDecreases() {
        cart.add(phoneCase, 1);

        boolean isRemoved = cart.remove("Case");

        assertAll(
                () -> assertTrue(isRemoved),
                () -> assertEquals(0, cart.getTotalInCents())
        );
    }

    // Discounts
    // TODO 11: "SAVE10" takes 10 percent off (use a total that divides well, like 30000)
    @Test
    void applyDiscountCode_save10_takesTenPercentOff() {
        cart.add(phone, 1);

        cart.applyDiscountCode("SAVE10");

        assertEquals(27000, cart.getTotalInCents());
    }

    // TODO 12: "HALF" takes 50 percent off
    @Test
    void applyDiscountCode_HALF_takesFiftyPercentOff() {
        cart.add(phone, 1);

        cart.applyDiscountCode("HALF");

        assertEquals(15000, cart.getTotalInCents());
    }

    // TODO 13: ROUNDING: 3 pens = 999 cents, with "SAVE10" the total is 900 (the discount 99.9 is rounded DOWN to 99)
    @Test
    void applyDiscountCode_save10x3_takesTenPercentOff() {
        cart.add(pen, 3);

        cart.applyDiscountCode("SAVE10");

        assertEquals(900, cart.getTotalInCents());
    }

    // TODO 14: a discount on an empty cart -> total is 0
    @Test
    void applyDiscountCode_toEmptyCart_totalRemainsSame() {

        cart.applyDiscountCode("SAVE10");

        assertEquals(0, cart.getTotalInCents());
    }

    // TODO 15: an unknown code (like "FREE") -> IllegalArgumentException, message "Unknown discount code"
    @Test
    void applyDiscountCode_unknownCode_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class ,() -> cart.applyDiscountCode("FREE"));
    }

    // TODO 16: a null code -> IllegalArgumentException
    @Test
    void applyDiscountCode_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class ,() -> cart.applyDiscountCode(null));
    }

    // TODO 17: codes do NOT add up: "SAVE10" and then "HALF" -> only 50 percent

    @Test
    void applyTwoDiscountCode_takesPercentOffOneCode() {
        cart.add(phone, 1);

        cart.applyDiscountCode("SAVE10");
        cart.applyDiscountCode("HALF");

        assertEquals(15000, cart.getTotalInCents());
    }
    // TODO 18: a WRONG code does not remove the discount that is already set:
//          apply "HALF", try "FREE" (it throws), the total is still half
    @Test
    void applyDiscountCode_wrongCodeDoesNotRemoveExistingDiscount() {
        cart.add(phone, 1);
        cart.applyDiscountCode("HALF");

        assertThrows(
                IllegalArgumentException.class, () -> cart.applyDiscountCode("FREE")        );

        assertEquals(
                15000, cart.getTotalInCents()
        );
    }
    // TODO 19: the discount is used for products added AFTER the code, too
    @Test
    void applyDiscountCode_beforeAddingProduct_discountApplies() {
        cart.applyDiscountCode("SAVE10");
        cart.add(phone, 1);

        assertEquals(
                27000, cart.getTotalInCents()
        );
    }
    // TODO 20: remove a product after the discount -> the total is recalculated with the discount
    @Test
    void remove_productAfterDiscount_totalRecalculatesWithDiscount() {
        cart.add(phone, 1);
        cart.add(phoneCase, 1);

        cart.applyDiscountCode("SAVE10");
        cart.remove("Phone");

        assertEquals(
                1350,
                cart.getTotalInCents()
        );
    }
}
