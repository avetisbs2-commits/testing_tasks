package com.example.integration.service;

import com.example.integration.entity.Book;
import com.example.integration.entity.Loan;
import com.example.integration.repository.BookRepository;
import com.example.integration.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The rules of the library.
 *
 * A TRANSACTION is "all or nothing": either every change in a method is saved, or (when an exception
 * happens) none of them is. @Transactional gives a method a transaction.
 *
 * This code is FINISHED. Your job is to test it with a REAL database (no mocks).
 * But be careful: in level 4 your tests will find that one method has a bug.
 */
@Service
public class LibraryService {

    /** A person can have at most this many books out at the same time. */
    public static final int MAX_ACTIVE_LOANS = 3;

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public LibraryService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    /** Adds a book. Throws IllegalStateException("ISBN already exists: <isbn>") if the ISBN is taken. */
    @Transactional
    public Book addBook(String title, String author, String isbn, int copies) {
        if (bookRepository.findByIsbn(isbn).isPresent()) {
            throw new IllegalStateException("ISBN already exists: " + isbn);
        }
        return bookRepository.save(new Book(title, author, isbn, copies));
    }

    /** Throws NoSuchElementException("Book not found: <id>") if there is no such book. */
    @Transactional(readOnly = true)
    public Book getBook(long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthorIgnoreCase(author);
    }

    @Transactional(readOnly = true)
    public List<Book> findAvailable() {
        return bookRepository.findAvailable();
    }

    /**
     * Lends one copy of a book to a person: one copy less on the shelf, and a new Loan (borrowed today).
     * Throws NoSuchElementException("Book not found: <id>")      if there is no such book.
     * Throws IllegalStateException("No copies available")        if all copies are out.
     * Throws IllegalStateException("Loan limit reached")         if the person already has MAX_ACTIVE_LOANS
     *                                                            books that are not returned.
     */
    @Transactional
    public Loan borrow(long bookId, String borrower) {
        Book book = getBook(bookId);
        if (book.getCopies() == 0) {
            throw new IllegalStateException("No copies available");
        }
        if (loanRepository.countByBorrowerAndReturnedOnIsNull(borrower) >= MAX_ACTIVE_LOANS) {
            throw new IllegalStateException("Loan limit reached");
        }
        book.setCopies(book.getCopies() - 1);
        bookRepository.save(book);
        return loanRepository.save(new Loan(book, borrower, LocalDate.now()));
    }

    /**
     * Lends several books to one person at the same time. It must be ALL OR NOTHING:
     * if one of the books cannot be lent (no copies, limit reached ...), then NO book is lent at all.
     */
    public List<Loan> borrowMany(List<Long> bookIds, String borrower) {
        List<Loan> loans = new ArrayList<>();
        for (long bookId : bookIds) {
            loans.add(borrow(bookId, borrower));
        }
        return loans;
    }

    /**
     * The person brings the book back: the loan gets returnedOn = today, and one more copy is on the shelf.
     * Throws NoSuchElementException("Loan not found: <id>")   if there is no such loan.
     * Throws IllegalStateException("Loan already returned")   if it was returned before.
     */
    @Transactional
    public Loan returnBook(long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NoSuchElementException("Loan not found: " + loanId));
        if (loan.getReturnedOn() != null) {
            throw new IllegalStateException("Loan already returned");
        }
        loan.setReturnedOn(LocalDate.now());
        Book book = loan.getBook();
        book.setCopies(book.getCopies() + 1);
        bookRepository.save(book);
        return loanRepository.save(loan);
    }

    /** The loans of this person that are not returned yet. */
    @Transactional(readOnly = true)
    public List<Loan> activeLoans(String borrower) {
        return loanRepository.findByBorrowerAndReturnedOnIsNull(borrower);
    }
}
