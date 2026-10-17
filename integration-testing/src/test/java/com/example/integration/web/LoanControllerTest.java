package com.example.integration.web;

import com.example.integration.entity.Book;
import com.example.integration.entity.Loan;
import com.example.integration.repository.BookRepository;
import com.example.integration.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LEVEL 3 (part 2) - THE LOAN ENDPOINTS, AND "BORROW MANY".
 *
 *   POST /api/loans/{id}/return
 *   GET  /api/loans?borrower=alice
 *   POST /api/borrow-many            body: {"bookIds": [1, 2], "borrower": "alice"}
 *
 * Prepare the data with the repositories:
 *     Book book = bookRepository.save(new Book("Dune", "Frank Herbert", "111", 2));
 *     Loan loan = loanRepository.save(new Loan(book, "alice", LocalDate.now()));
 * Then use loan.getId() / book.getId() in the URL:   post("/api/loans/" + loan.getId() + "/return")
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    // ---------------------------------------------------------------
    // WORKED EXAMPLE
    // ---------------------------------------------------------------
    @Test
    void getLoans_ofAPersonWithoutLoans_returnsAnEmptyList() throws Exception {
        mockMvc.perform(get("/api/loans").param("borrower", "nobody"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------------------------------------------------------------
    // YOUR TESTS
    // ---------------------------------------------------------------

    // POST /api/loans/{id}/return
    // TODO 1:  a loan that is out -> 200, the JSON has returnedOn = today
    // TODO 2:  ... and the book has one more copy in the DATABASE
    // TODO 3:  a loan id that does not exist -> 404, "Loan not found: 9999"
    // TODO 4:  returning the same loan twice -> the first 200, the second 409 "Loan already returned"

    // GET /api/loans?borrower=...
    // TODO 5:  alice has 2 active loans and 1 returned loan -> the list has 2 (only the active ones)
    // TODO 6:  the loans of alice and bob are not mixed
    // TODO 7:  there is NO borrower parameter at all -> 400   (Spring answers this one)

    // POST /api/borrow-many
    // TODO 8:  two books with copies -> 201 and a JSON list of 2 loans; both books have one copy less
    // TODO 9:  an EMPTY bookIds list [] -> 400  (@NotEmpty)
    // TODO 10: a blank borrower -> 400
    // TODO 11: a book id that does not exist -> 404
    //          (this test only looks at the STATUS. What happened to the OTHER books is a question for level 4!)
}
