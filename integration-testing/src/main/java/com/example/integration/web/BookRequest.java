package com.example.integration.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** The JSON that creates a book. PROVIDED CODE - do not change. */
public record BookRequest(
        @NotBlank String title,
        @NotBlank String author,
        @NotBlank String isbn,
        @Min(0) int copies) {
}
