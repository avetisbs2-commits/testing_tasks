package com.example.integration.repository;

import com.example.integration.entity.Book;
import com.example.integration.entity.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * LEVEL 1 (part 2) - A REPOSITORY WITH A RELATION.
 *
 * A Loan points to a Book (@ManyToOne). So to save a loan you must save its book FIRST.
 * "Active loan" means returnedOn is null.
 *
 * Tools: the same as in BookRepositoryTest.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LoanRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    private Book dune;
    private Book emma;

    @BeforeEach
    void setUp() {
        dune = bookRepository.save(new Book("Dune", "Frank Herbert", "111", 2));
        emma = bookRepository.save(new Book("Emma", "Jane Austen", "222", 1));
    }

    // ---------------------------------------------------------------
    // WORKED EXAMPLE
    // ---------------------------------------------------------------
    @Test
    void save_aNewLoan_hasNoReturnDate() {
        Loan saved = loanRepository.save(new Loan(dune, "alice", LocalDate.of(2026, 1, 10)));

        Loan found = loanRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getBorrower()).isEqualTo("alice");
        assertThat(found.getBook().getTitle()).isEqualTo("Dune");
        assertThat(found.getReturnedOn()).isNull();
    }

    // ---------------------------------------------------------------
    // YOUR TESTS
    // ---------------------------------------------------------------

    // findByBorrowerAndReturnedOnIsNull
    // TODO 1: alice has two loans, one of them is returned (setReturnedOn) -> only the ACTIVE one is in the list
    // TODO 2: alice and bob both have an active loan -> asking for "alice" returns ONLY alice's loan
    // TODO 3: a borrower without loans -> an EMPTY list
    // TODO 4: the loaded loan knows its book (check loan.getBook().getTitle())

    // countByBorrowerAndReturnedOnIsNull
    // TODO 5: the count is the same as the size of the list from the method above
    // TODO 6: returned loans are not counted; for an unknown borrower the count is 0

    // The rules of the database
    // TODO 7: a loan WITHOUT a book (null) -> saveAndFlush throws DataIntegrityViolationException
    //         (the relation is "optional = false")
    // TODO 8: a loan with borrower null -> saveAndFlush throws an exception too
    //         (tip: a RuntimeException or DataIntegrityViolationException. Try it and read the message)
}
