package com.example.integration.web;

import com.example.integration.entity.Book;
import com.example.integration.entity.Loan;
import com.example.integration.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * The REST API of the books.
 *
 *   POST /api/books                  create a book                   -> 201
 *   GET  /api/books                  all books (or ?author=...)      -> 200
 *   GET  /api/books/available        books with a copy on the shelf  -> 200
 *   GET  /api/books/{id}             one book                        -> 200, or 404
 *   POST /api/books/{id}/borrow      lend a book                     -> 201, or 404, 409
 *   POST /api/borrow-many            lend several books at once      -> 201, or 404, 409
 *
 * Errors are answered by ApiExceptionHandler: 400 bad data, 404 not found, 409 conflict.
 *
 * PROVIDED CODE - do not change.
 */
@RestController
@RequestMapping("/api")
public class BookController {

    private final LibraryService service;

    public BookController(LibraryService service) {
        this.service = service;
    }

    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@Valid @RequestBody BookRequest request) {
        Book book = service.addBook(request.title(), request.author(), request.isbn(), request.copies());
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @GetMapping("/books")
    public List<Book> listBooks(@RequestParam(name = "author", required = false) String author) {
        if (author == null) {
            return service.findAll();
        }
        return service.findByAuthor(author);
    }

    @GetMapping("/books/available")
    public List<Book> availableBooks() {
        return service.findAvailable();
    }

    @GetMapping("/books/{id}")
    public Book getBook(@PathVariable("id") long id) {
        return service.getBook(id);
    }

    @PostMapping("/books/{id}/borrow")
    public ResponseEntity<LoanResponse> borrow(@PathVariable("id") long id,
                                               @Valid @RequestBody BorrowRequest request) {
        Loan loan = service.borrow(id, request.borrower());
        return ResponseEntity.status(HttpStatus.CREATED).body(LoanResponse.from(loan));
    }

    @PostMapping("/borrow-many")
    public ResponseEntity<List<LoanResponse>> borrowMany(@Valid @RequestBody BorrowManyRequest request) {
        List<LoanResponse> loans = new ArrayList<>();
        for (Loan loan : service.borrowMany(request.bookIds(), request.borrower())) {
            loans.add(LoanResponse.from(loan));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(loans);
    }
}
