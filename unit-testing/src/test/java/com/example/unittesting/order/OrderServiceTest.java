package com.example.unittesting.order;

import com.example.unittesting.user.EmailSender;
import com.example.unittesting.user.UserRepository;
import com.example.unittesting.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.*;
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

    private Inventory inventory;
    private PaymentGateway paymentGateway;
    private Notifier notifier;
    private OrderService service;

    @BeforeEach
    void setUp() {
        inventory = mock(Inventory.class);
        paymentGateway = mock(PaymentGateway.class);
        notifier = mock(Notifier.class);
        service = new OrderService(inventory, paymentGateway , notifier);
    }

    // TODO 1:  quantity 0 (and negative)  -> IllegalArgumentException "Quantity must be positive",
    //          and NO helper is used at all.
    @Test
    void placeOrder_lessQuantityThanZero_throwsIllegalArgumentException(){
        var e = assertThrows(IllegalArgumentException.class , () -> service.placeOrder("1" , "1" , -2));
        assertEquals( "Quantity must be positive" , e.getMessage());
        verifyNoInteractions(inventory, paymentGateway, notifier);
    }
    // TODO 2:  not in stock               -> OUT_OF_STOCK; no payment, no reserve, no message.
    @Test
    void placeOrder_notInStock_outOfStock(){
        when(inventory.isAvailable("1", 2)).thenReturn(false);

        OrderResult result = service.placeOrder("1", "1", 2);

        assertEquals(OrderResult.OUT_OF_STOCK, result);

        verify(inventory).isAvailable("1", 2);

        verify(inventory, never()).reserve(anyString(), anyInt());

        verifyNoInteractions(paymentGateway, notifier);

    }
    // TODO 3:  success, a small order     -> SUCCESS; charged the exact price x quantity;
    //          reserved; the confirmation has the same amount.
    @Test
    void placeOrder_everythingIsGood_success(){
        when(inventory.isAvailable("1", 2)).thenReturn(true);
        when(inventory.getPriceInCents("1")).thenReturn(1000);
        when(paymentGateway.charge("1" , 2000)).thenReturn(true);

        OrderResult result = service.placeOrder("1", "1", 2);
        assertEquals(OrderResult.SUCCESS, result);

        verify(paymentGateway).charge("1" , 2000);
        verify(inventory).reserve("1", 2);
        verify(notifier).sendConfirmation("1", 2000);
    }

    // TODO 4:  the payment is declined    -> PAYMENT_DECLINED; nothing reserved, no message, no refund.
    @Test
    void placeOrder_paymentIsDeclined_paymentDeclined(){
        when(inventory.isAvailable("1", 2)).thenReturn(true);
        when(paymentGateway.charge("1", 1000)).thenReturn(false);
        when(inventory.getPriceInCents("1")).thenReturn(500);

        OrderResult result = service.placeOrder("1", "1", 2);

        assertEquals(OrderResult.PAYMENT_DECLINED, result);

        verify(inventory, never()).reserve(anyString(), anyInt());
        verify(paymentGateway, never()).refund(anyString(), anyInt());
        verify(notifier, never()).sendConfirmation(anyString() , anyInt());
    }
    // TODO 5:  reserve fails (RuntimeException) -> FAILED; the SAME amount that was charged is refunded; no message.
    @Test
    void placeOrder_reserveFails_failed(){
        when(inventory.isAvailable("1", 2)).thenReturn(true);
        when(paymentGateway.charge("1", 1000)).thenReturn(true);
        when(inventory.getPriceInCents("1")).thenReturn(500);

        doThrow(new RuntimeException("Reservation failed")).when(inventory).reserve("1", 2);

        OrderResult result = service.placeOrder("1", "1", 2);
        assertEquals(OrderResult.FAILED, result);


        verify(paymentGateway).refund("1", 1000);
        verify(notifier, never()).sendConfirmation(anyString() , anyInt());
    }
    // TODO 6:  the discount boundary: 9999 cents total -> charged 9999 (no discount)
    @Test
    void placeOrder_total9999_noDiscount() {
        when(inventory.isAvailable("1", 1)).thenReturn(true);
        when(inventory.getPriceInCents("1")).thenReturn(9999);
        when(paymentGateway.charge("1", 9999)).thenReturn(true);

        OrderResult result = service.placeOrder("1", "1", 1);

        assertEquals(OrderResult.SUCCESS, result);
        verify(paymentGateway).charge("1", 9999);
    }
    // TODO 7:  the discount boundary: 10000 cents total -> charged 9000
    @Test
    void placeOrder_total10000_appliesDiscount() {
        when(inventory.isAvailable("1", 1)).thenReturn(true);
        when(inventory.getPriceInCents("1")).thenReturn(10000);
        when(paymentGateway.charge("1", 9000)).thenReturn(true);

        OrderResult result = service.placeOrder("1", "1", 1);

        assertEquals(OrderResult.SUCCESS, result);
        verify(paymentGateway).charge("1", 9000);
    }
    // TODO 8:  above the boundary: 10001 cents total -> charged 9001   (10001 - 1000, the discount is rounded DOWN)
    @Test
    void placeOrder_total10001_discountRoundedDown() {
        when(inventory.isAvailable("1", 1)).thenReturn(true);
        when(inventory.getPriceInCents("1")).thenReturn(10001);
        when(paymentGateway.charge("1", 9001)).thenReturn(true);

        OrderResult result = service.placeOrder("1", "1", 1);

        assertEquals(OrderResult.SUCCESS, result);
        verify(paymentGateway).charge("1", 9001);
    }

    // TODO 9:  the discount is also used for the refund and for the confirmation (same amount everywhere)
    @Test
    void placeOrder_reserveFails_failed_refundDiscount(){
        when(inventory.isAvailable("1", 1)).thenReturn(true);
        when(paymentGateway.charge("1", 9000)).thenReturn(true);
        when(inventory.getPriceInCents("1")).thenReturn(10000);

        doThrow(new RuntimeException("Reservation failed")).when(inventory).reserve("1", 1);

        OrderResult result = service.placeOrder("1", "1", 1);
        assertEquals(OrderResult.FAILED, result);

        verify(paymentGateway).charge("1", 9000);
        verify(paymentGateway).refund("1", 9000);
        verify(notifier, never()).sendConfirmation(anyString() , anyInt());
    }

    @Test
    void placeOrder_total10000_success_confirmationDiscount(){
        when(inventory.isAvailable("1", 1)).thenReturn(true);
        when(paymentGateway.charge("1", 9000)).thenReturn(true);
        when(inventory.getPriceInCents("1")).thenReturn(10000);

        OrderResult result = service.placeOrder("1", "1", 1);
        assertEquals(OrderResult.SUCCESS, result);


        verify(paymentGateway,never()).refund("1", 9000);
        verify(notifier ).sendConfirmation("1", 9000);
    }
    // TODO 10: the order of the calls on success: charge, then reserve, then sendConfirmation (InOrder)
    @Test
    void placeOrder_success_callsInCorrectOrder() {
        when(inventory.isAvailable("1", 1)).thenReturn(true);
        when(inventory.getPriceInCents("1")).thenReturn(10000);
        when(paymentGateway.charge("1", 9000)).thenReturn(true);

        OrderResult result = service.placeOrder("1", "1", 1);

        assertEquals(OrderResult.SUCCESS, result);

        InOrder order = inOrder(paymentGateway, inventory, notifier);

        order.verify(paymentGateway).charge("1", 9000);
        order.verify(inventory).reserve("1", 1);
        order.verify(notifier).sendConfirmation("1", 9000);
    }
    // TODO 11: on a success, nothing else happened: verifyNoMoreInteractions(...)
    //          (hint: this one forces you to verify every call. It is hard, but it is a great check.)
    @Test
    void placeOrder_success_verifyNoMoreInteractions() {
        when(inventory.isAvailable("1", 1)).thenReturn(true);
        when(inventory.getPriceInCents("1")).thenReturn(10000);
        when(paymentGateway.charge("1", 9000)).thenReturn(true);

        OrderResult result = service.placeOrder("1", "1", 1);
        assertEquals(OrderResult.SUCCESS, result);

        verify(inventory).isAvailable("1", 1);
        verify(inventory).getPriceInCents("1");
        verify(paymentGateway).charge("1", 9000);
        verify(inventory).reserve("1", 1);
        verify(notifier).sendConfirmation("1", 9000);

        verifyNoMoreInteractions(inventory, paymentGateway, notifier);
    }
}
