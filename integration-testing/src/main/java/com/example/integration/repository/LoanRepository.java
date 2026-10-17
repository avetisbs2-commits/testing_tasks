package com.example.integration.repository;

import com.example.integration.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Talks to the "loans" table. PROVIDED CODE - do not change. */
public interface LoanRepository extends JpaRepository<Loan, Long> {

    /** The loans of one person that are NOT returned yet (returnedOn is null). */
    List<Loan> findByBorrowerAndReturnedOnIsNull(String borrower);

    long countByBorrowerAndReturnedOnIsNull(String borrower);
}
