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
    @Test
    void findByBorrowerAndReturnedOnIsNull_twoLoans_returnActiveLoan() {
        Loan saved = loanRepository.
                save(new Loan(dune, "alice", LocalDate.of(2026, 1, 10)));
        loanRepository.save(new Loan(emma , "alice", LocalDate.of(2026, 1, 10)));

        saved.setReturnedOn(LocalDate.of(2026, 2, 10));
        assertThat(loanRepository.findByBorrowerAndReturnedOnIsNull("alice")).hasSize(1);
    }
    // TODO 2: alice and bob both have an active loan -> asking for "alice" returns ONLY alice's loan
    @Test
    void findByBorrowerAndReturnedOnIsNull_twoBorrowers_returnOnlyAskedActiveLoan() {
        loanRepository.save(new Loan(dune, "alice", LocalDate.of(2026, 1, 10)));
        loanRepository.save(new Loan(emma , "bob", LocalDate.of(2026, 1, 10)));

        assertThat(loanRepository.findByBorrowerAndReturnedOnIsNull("alice")).hasSize(1);
    }
    // TODO 3: a borrower without loans -> an EMPTY list
    @Test
    void findByBorrowerAndReturnedOnIsNull_noActiveLoans_returnEmptyList() {
        assertThat(loanRepository.findByBorrowerAndReturnedOnIsNull("alice")).isEmpty();
        assertThat(loanRepository.findByBorrowerAndReturnedOnIsNull("alice")).isNotNull();
    }
    // TODO 4: the loaded loan knows its book (check loan.getBook().getTitle())
    @Test
    void findByBorrowerAndReturnedOnIsNull_activeLoan_checkLoanGetBooGetTitle() {
        Loan loan = loanRepository.
                save(new Loan(dune, "alice", LocalDate.of(2026, 1, 10)));

        assertThat(loan.getBook().getTitle()).isEqualTo(dune.getTitle());
    }

    // countByBorrowerAndReturnedOnIsNull
    // TODO 5: the count is the same as the size of the list from the method above
    @Test
    void countByBorrowerAndReturnedOnIsNull_existedBorrower_returnActiveLoansCount() {
        Loan loan = loanRepository.
                save(new Loan(dune, "alice", LocalDate.of(2026, 1, 10)));

        assertThat(loanRepository.countByBorrowerAndReturnedOnIsNull("alice")).isEqualTo(1);
    }
    // TODO 6: returned loans are not counted; for an unknown borrower the count is 0
    @Test
    void countByBorrowerAndReturnedOnIsNull_existedBorrowerWithReturnedLoan_returnActiveLoansCount() {
        Loan loan = loanRepository.
                save(new Loan(dune, "alice", LocalDate.of(2026, 1, 10)));

        loan.setReturnedOn(LocalDate.of(2026, 2, 10));

        assertThat(loanRepository.countByBorrowerAndReturnedOnIsNull("alice")).isEqualTo(0);
    }

    // The rules of the database
    // TODO 7: a loan WITHOUT a book (null) -> saveAndFlush throws DataIntegrityViolationException
    //         (the relation is "optional = false")
    @Test
    void save_loanWithoutBook_throwsDataIntegrityViolationException() {

        assertThatThrownBy(() -> loanRepository.saveAndFlush(new Loan(null, "alice", LocalDate.of(2026, 1, 10)))
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    // TODO 8: a loan with borrower null -> saveAndFlush throws an exception too
    //         (tip: a RuntimeException or DataIntegrityViolationException. Try it and read the message)
    @Test
    void save_loanWithoutBorrower_throwsDataIntegrityViolationException() {

        assertThatThrownBy(() -> loanRepository.saveAndFlush(new Loan(dune, null, LocalDate.of(2026, 1, 10)))
        ).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> loanRepository.saveAndFlush(new Loan(dune, null, LocalDate.of(2026, 1, 10)))
        ).isInstanceOf(RuntimeException.class);
    }
}
