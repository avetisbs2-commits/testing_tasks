package com.example.integration.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** The JSON that lends several books at once. PROVIDED CODE - do not change. */
public record BorrowManyRequest(@NotEmpty List<Long> bookIds, @NotBlank String borrower) {
}
