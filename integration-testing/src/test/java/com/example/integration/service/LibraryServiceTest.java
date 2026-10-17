package com.example.integration.service;

import com.example.integration.entity.Book;
import com.example.integration.entity.Loan;
import com.example.integration.repository.BookRepository;
import com.example.integration.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * LEVEL 2 - SERVICE + REPOSITORIES + DATABASE, ALL REAL.
 *
 * In unit tests (level 6 of the first module) you replaced the repository with a MOCK.
 * Here you use the REAL repositories and a REAL database, so you also test the queries, the mapping,
 * and the transaction together with the business rules.
 *
 * @SpringBootTest   starts the WHOLE application (a bit slower, but everything is real).
 * @Transactional    on a TEST CLASS: every test runs in a transaction that is ROLLED BACK at the end,
 *                   so every test starts with an empty database. (Careful: in level 4 you will see
 *                   why this is not always what you want.)
 *
 * Your questions for every test:
 *   1. does the method return the right thing?
 *   2. what is in the DATABASE after it? (ask the repositories: count(), findById(...))
 *   3. when it fails, did it leave the database UNCHANGED?
 */
@SpringBootTest
@Transactional
class LibraryServiceTest {

    @Autowired
    private LibraryService service;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    // ---------------------------------------------------------------
    // WORKED EXAMPLE
    // ---------------------------------------------------------------
    @Test
    void addBook_savesTheBookInTheDatabase() {
        Book book = service.addBook("Dune", "Frank Herbert", "111", 2);

        assertThat(book.getId()).isNotNull();
        assertThat(bookRepository.findByIsbn("111")).isPresent();
        assertThat(bookRepository.count()).isEqualTo(1);
    }

    // ---------------------------------------------------------------
    // YOUR TESTS
    // ---------------------------------------------------------------

    // addBook / getBook / the finders
    // TODO 1:  addBook with an ISBN that exists -> IllegalStateException "ISBN already exists: 111",
    //          and there is still ONE book in the database
    // TODO 2:  getBook(id) of a saved book returns it
    // TODO 3:  getBook with an id that does not exist -> NoSuchElementException "Book not found: 9999"
    // TODO 4:  findAll returns all the saved books
    // TODO 5:  findByAuthor returns only the books of that author (case does not matter)
    // TODO 6:  findAvailable skips the books with 0 copies

    // borrow
    // TODO 7:  borrow a book with 2 copies: the book has 1 copy in the DATABASE now (reload it with the repository),
    //          and the returned Loan has the right borrower, the book, borrowedOn = today (LocalDate.now()), returnedOn = null
    // TODO 8:  borrow the LAST copy: copies is 0
    // TODO 9:  borrow a book with 0 copies -> IllegalStateException "No copies available",
    //          and NO loan was saved (loanRepository.count() is 0)
    // TODO 10: borrow a book that does not exist -> NoSuchElementException
    // TODO 11: THE LIMIT. A person can have 3 active loans (LibraryService.MAX_ACTIVE_LOANS).
    //          Borrow 3 different books -> all fine.  The 4th -> IllegalStateException "Loan limit reached",
    //          and the copies of the 4th book are unchanged.     <- the boundary: 3 is allowed, 4 is not
    // TODO 12: the limit is PER PERSON: when alice has 3 loans, bob can still borrow
    // TODO 13: a RETURNED loan does not count for the limit: alice has 3 loans, returns one, can borrow again

    // returnBook
    // TODO 14: returnBook: the loan has returnedOn = today, and the book has one more copy (check the DATABASE)
    // TODO 15: returnBook with an id that does not exist -> NoSuchElementException "Loan not found: 9999"
    // TODO 16: returnBook TWICE -> the second time IllegalStateException "Loan already returned",
    //          and the copies were increased only ONCE

    // activeLoans
    // TODO 17: activeLoans("alice") shows the loans that are not returned, and only of alice

    // borrowMany  (only the "everything works" case here. The "something fails" case is for level 4!)
    // TODO 18: borrowMany of two books: two loans are returned, both books have one copy less
}
