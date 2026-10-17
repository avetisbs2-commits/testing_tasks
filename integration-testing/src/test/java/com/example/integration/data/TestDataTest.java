package com.example.integration.data;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LEVEL 5 - TEST DATA AND ISOLATION.
 *
 * Building the data in Java (many save() calls) gets long and hard to read. A better way for a bigger
 * set of data: an SQL script. @Sql runs a script BEFORE every test of this class.
 *
 *    src/test/resources/sql/library-data.sql   (PROVIDED - open it and read it first!)
 *
 * The data:
 *    Dune (2 copies)  Emma (0)  Persuasion (1)  The Hobbit (5)  Ulysses (1)
 *    Jane Austen wrote Emma and Persuasion.
 *    alice has 3 active loans (Dune, Persuasion, The Hobbit)  = the maximum.
 *    bob has one RETURNED loan (Emma) and no active loans.
 *
 * THE PROBLEM OF THIS LEVEL:  ISOLATION.
 *    Every test must work ALONE and in ANY order. The test methods run in a RANDOM order (see
 *    src/test/resources/junit-platform.properties). Add a second test and run the class a few times.
 *    What happens? The script inserts the same ISBNs again, but the data of the first test is still
 *    there... Read the error message, and solve it. TWO good solutions:
 *
 *    A) Clean up AFTER every test with a second script, in this order of the annotations' attributes:
 *           @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
 *       Write the SQL in src/test/resources/sql/cleanup.sql  (careful: loans point to books).
 *
 *    B) Put @Transactional on the class. Then the script and the test run in one transaction that is
 *       rolled back after every test. Nothing to clean. (Not possible when you must see a real "commit"
 *       or "rollback" of the application, like in level 4.)
 *
 *    Choose A for this class and say in a comment why. (Try B in a second class if you like.)
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql("/sql/library-data.sql")
class TestDataTest {

    @Autowired
    private MockMvc mockMvc;

    // ---------------------------------------------------------------
    // WORKED EXAMPLE
    // ---------------------------------------------------------------
    @Test
    void availableBooks_fromTheTestData_skipTheEmptyShelf_andAreSortedByTitle() throws Exception {
        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].title").value("Dune"))
                .andExpect(jsonPath("$[3].title").value("Ulysses"));
    }

    // ---------------------------------------------------------------
    // YOUR TESTS (they all use the test data above)
    // ---------------------------------------------------------------

    // TODO 1: FIRST fix the isolation (see above) so that you can have more than one test in this class.

    // TODO 2: GET /api/books?author=jane austen -> 2 books: Emma and Persuasion
    // TODO 3: GET /api/loans?borrower=alice -> 3 active loans
    // TODO 4: GET /api/loans?borrower=bob -> 0 active loans (his loan is returned)
    // TODO 5: alice is at the limit: POST /api/books/{Ulysses id}/borrow {"borrower": "alice"} -> 409 "Loan limit reached"
    //         (you need the id of Ulysses: GET /api/books and read it from the JSON, or use bookRepository.findByIsbn("555"))
    // TODO 6: bob borrows Ulysses -> 201. Bob's returned loan did NOT count for the limit.
    // TODO 7: nobody can borrow Emma (0 copies) -> 409 "No copies available"
    // TODO 8: alice returns her loan of Dune (find its id with GET /api/loans?borrower=alice) -> 200,
    //         and now Dune has 3 copies in GET /api/books/{id}
    // TODO 9: after TODO 8's return, alice can borrow Ulysses (201): she is under the limit again.
    //         (This test must NOT see the changes of the test before it: that is isolation!)
}
