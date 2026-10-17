package com.example.integration.web;

import com.example.integration.entity.Loan;

import java.time.LocalDate;

/** The JSON of a loan. Dates look like "2026-10-17". PROVIDED CODE - do not change. */
public record LoanResponse(
        Long id,
        Long bookId,
        String bookTitle,
        String borrower,
        LocalDate borrowedOn,
        LocalDate returnedOn) {

    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getBorrower(),
                loan.getBorrowedOn(),
                loan.getReturnedOn());
    }
}
