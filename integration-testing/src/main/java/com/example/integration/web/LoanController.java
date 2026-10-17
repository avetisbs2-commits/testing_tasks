package com.example.integration.web;

import com.example.integration.entity.Loan;
import com.example.integration.service.LibraryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * The REST API of the loans.
 *
 *   POST /api/loans/{id}/return      the person brings the book back -> 200, or 404, 409
 *   GET  /api/loans?borrower=...     the loans that are not returned -> 200
 *
 * PROVIDED CODE - do not change.
 */
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LibraryService service;

    public LoanController(LibraryService service) {
        this.service = service;
    }

    @PostMapping("/{id}/return")
    public LoanResponse returnBook(@PathVariable("id") long id) {
        return LoanResponse.from(service.returnBook(id));
    }

    @GetMapping
    public List<LoanResponse> activeLoans(@RequestParam("borrower") String borrower) {
        List<LoanResponse> result = new ArrayList<>();
        for (Loan loan : service.activeLoans(borrower)) {
            result.add(LoanResponse.from(loan));
        }
        return result;
    }
}
