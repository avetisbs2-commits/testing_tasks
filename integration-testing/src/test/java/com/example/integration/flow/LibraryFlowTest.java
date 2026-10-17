package com.example.integration.flow;

import com.example.integration.repository.BookRepository;
import com.example.integration.repository.LoanRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LEVEL 4 - WHOLE STORIES, AND A BUG HUNT.
 *
 * Until now every test did ONE thing. Here a test tells a STORY with many steps, only through the
 * HTTP API, like a real client would: add a book -> borrow it -> look at the shelf -> bring it back.
 * Integration tests are very good at finding bugs between the steps.
 *
 * IMPORTANT: this class has NO @Transactional.
 * Why? A transaction is "all or nothing". If the TEST runs inside a transaction, it hides what the
 * application does with ITS OWN transactions: everything is rolled back at the end, and nothing is ever
 * really "committed". To test what is really saved (or NOT saved) after an error, the test must NOT
 * be in a transaction. The price: the data stays in the database, so YOU clean it. The two methods
 * at the bottom of the setup do it for you (PROVIDED).
 *
 * Helper idea: write small private methods to make the stories short and readable:
 *     private long addBook(String isbn, int copies) throws Exception { ... returns the id from the JSON ... }
 *     (tip: .andReturn().getResponse().getContentAsString() gives the JSON text; JsonPath.read(text, "$.id")
 *      reads a value from it; com.jayway.jsonpath.JsonPath is on the classpath)
 */
@SpringBootTest
@AutoConfigureMockMvc
class LibraryFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    // PROVIDED - the database is empty before and after every test (loans first: they point to books)
    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
    }

    // ---------------------------------------------------------------
    // WORKED EXAMPLE
    // ---------------------------------------------------------------
    @Test
    void story_addABook_thenFindItInTheList() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Dune", "author": "Frank Herbert", "isbn": "111", "copies": 2}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Dune"));
    }

    // ---------------------------------------------------------------
    // YOUR TESTS - the stories
    // ---------------------------------------------------------------

    // TODO 1: THE LIFE OF A LOAN.  add a book with 1 copy -> it is in /api/books/available
    //         -> alice borrows it -> it is NOT in /available any more, and alice has 1 active loan
    //         -> alice returns the loan -> it IS in /available again, and alice has 0 active loans.

    // TODO 2: THE LIMIT. add 4 books. alice borrows 3 (all 201), the 4th is 409 "Loan limit reached".
    //         alice returns one -> now the 4th borrow works (201).

    // TODO 3: TWO PEOPLE, ONE COPY. a book with 1 copy: alice borrows it (201), bob tries -> 409 "No copies available".
    //         alice returns it -> bob borrows it (201).

    // ---------------------------------------------------------------
    // THE BUG HUNT  (borrow-many must be ALL OR NOTHING)
    // ---------------------------------------------------------------
    // POST /api/borrow-many lends several books in one request. The rule says: if ONE book cannot be lent,
    // then NO book must be lent. The customer must not get half of the books.
    //
    // TODO 4: add book A (1 copy), book B (1 copy), book C (0 copies).
    //         POST /api/borrow-many  {"bookIds": [A, B, C], "borrower": "alice"}   -> 409 "No copies available"
    //         THEN CHECK THAT NOTHING CHANGED:   A still has 1 copy, B still has 1 copy (GET /api/books/{id}),
    //         and alice has 0 active loans (GET /api/loans?borrower=alice).
    //
    // TODO 5: the same idea with the LIMIT: alice already has 2 active loans. She asks for 2 more books in one
    //         request. The first one is allowed, the second one is over the limit -> 409 "Loan limit reached".
    //         After that alice must still have exactly 2 active loans, and the first book must still have all its copies.
    //
    // TODO 6: an unknown id in the list: [A, 9999] -> 404, and A still has all its copies.
    //
    // >>> RUN YOUR TESTS. If the tests of TODO 4, 5 and 6 are RED, you found a REAL BUG, in the application. <<<
    // The unit tests with mocks could NOT find it, because it needs a real database and real transactions.
    // Now FIND the bug in src/main (look at LibraryService.borrowMany and compare it with borrow), FIX it,
    // and run the tests again. They must turn green.
    //
    // TODO 7: borrow-many, the happy case: two books with copies -> 201, 2 loans, and both books have one copy less.
}
