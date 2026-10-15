package com.example.unittesting.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * LEVEL 7 - THE BOSS.
 *
 * OrderService has 3 helpers (all mocks), 6 steps, a discount boundary and a rollback.
 * Read the comment of placeOrder() in OrderService.java very carefully. It is your specification.
 *
 * There is NO example and NO setUp this time. You build everything.
 *
 * What a very good test suite for this class does:
 *   - one test for EVERY way the method can end (4 results + 1 exception)
 *   - for every ending, it checks the helpers that MUST be called (verify) AND the helpers that
 *     must NEVER be called (never / verifyNoInteractions). The wrong calls are the dangerous bugs:
 *     "charged the customer, but the order failed" is a real, expensive bug.
 *   - the BOUNDARY of the big-order discount: 9999 / 10000 / 10001 cents
 *   - the exact AMOUNT that is charged, refunded and written in the confirmation
 *   - the ORDER of the calls (use InOrder), for example: charge happens BEFORE reserve
 *
 * Tools you will need:
 *   when(...).thenReturn(...)                       stubbing a method that returns something
 *   doThrow(new RuntimeException("x")).when(inventory).reserve("p1", 2);   stubbing a void method that fails
 *   verify(mock).method(args)       verify(mock, never()).method(...)      verifyNoInteractions(mock)
 *   verifyNoMoreInteractions(mock)  fails if the mock was used in a way you did not verify
 *   InOrder order = inOrder(a, b);  order.verify(a).m1();  order.verify(b).m2();
 *   anyString(), anyInt()           "any value" when you do not care about the argument
 */
class OrderServiceTest {

    // TODO 0: declare the 3 mocks (Inventory, PaymentGateway, Notifier) and the OrderService,
    //         and create them in a @BeforeEach method, like in UserServiceTest.

    // TODO 1:  quantity 0 (and negative)  -> IllegalArgumentException "Quantity must be positive",
    //          and NO helper is used at all.
    // TODO 2:  not in stock               -> OUT_OF_STOCK; no payment, no reserve, no message.
    // TODO 3:  success, a small order     -> SUCCESS; charged the exact price x quantity;
    //          reserved; the confirmation has the same amount.
    // TODO 4:  the payment is declined    -> PAYMENT_DECLINED; nothing reserved, no message, no refund.
    // TODO 5:  reserve fails (RuntimeException) -> FAILED; the SAME amount that was charged is refunded; no message.
    // TODO 6:  the discount boundary: 9999 cents total -> charged 9999 (no discount)
    // TODO 7:  the discount boundary: 10000 cents total -> charged 9000
    // TODO 8:  above the boundary: 10001 cents total -> charged 9001   (10001 - 1000, the discount is rounded DOWN)
    // TODO 9:  the discount is also used for the refund and for the confirmation (same amount everywhere)
    // TODO 10: the order of the calls on success: charge, then reserve, then sendConfirmation (InOrder)
    // TODO 11: on a success, nothing else happened: verifyNoMoreInteractions(...)
    //          (hint: this one forces you to verify every call. It is hard, but it is a great check.)
}
