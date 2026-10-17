package com.example.integration.web;

import jakarta.validation.constraints.NotBlank;

/** The JSON that says who borrows a book. PROVIDED CODE - do not change. */
public record BorrowRequest(@NotBlank String borrower) {
}
