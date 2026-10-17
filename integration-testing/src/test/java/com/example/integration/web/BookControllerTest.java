package com.example.integration.web;

import com.example.integration.entity.Book;
import com.example.integration.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LEVEL 3 - TEST THE REST API OVER (SIMULATED) HTTP.
 *
 * You send a real HTTP request to the application, and check the HTTP answer: the STATUS CODE and the JSON.
 * Controller + validation + service + repository + database all run for real. Only the network is simulated.
 *
 * MockMvc  (the name is confusing: it has NOTHING to do with Mockito mocks!)
 *   mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(json))
 *          .andExpect(status().isCreated())                    // 201
 *          .andExpect(jsonPath("$.title").value("Dune"))       // a field of the JSON body
 *          .andExpect(jsonPath("$.length()").value(2))         // the size of a JSON list
 *   Other status methods: isOk() 200, isCreated() 201, isBadRequest() 400, isNotFound() 404, isConflict() 409.
 *
 * To prepare data you can use the REPOSITORY directly (faster and simpler than many POST requests).
 * Text blocks make JSON easy to write in Java:
 *     String json = """
 *         {"title": "Dune", "author": "Frank Herbert", "isbn": "111", "copies": 2}
 *         """;
 *
 * Questions for every test: the STATUS, the JSON, and (for a POST) what is in the DATABASE after.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    private static final String DUNE_JSON = """
            {"title": "Dune", "author": "Frank Herbert", "isbn": "111", "copies": 2}
            """;

    // ---------------------------------------------------------------
    // WORKED EXAMPLE
    // ---------------------------------------------------------------
    @Test
    void postBook_withValidJson_returns201AndTheBook() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DUNE_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Dune"))
                .andExpect(jsonPath("$.copies").value(2));
    }

    // ---------------------------------------------------------------
    // YOUR TESTS
    // ---------------------------------------------------------------

    // POST /api/books
    // TODO 1:  after a valid POST the book is in the DATABASE (bookRepository.findByIsbn)
    // TODO 2:  a blank title ("") -> 400, and the error JSON says {"error": "Validation failed"}
    // TODO 3:  a missing author, a blank isbn, and copies = -1 -> 400 each (a @ParameterizedTest is a good idea)
    // TODO 4:  after a 400 NOTHING was saved (bookRepository.count() is 0)
    // TODO 5:  an ISBN that exists already -> 409, and the error says "ISBN already exists: 111"
    // TODO 6:  copies = 0 is allowed -> 201   (the boundary of @Min(0))
    // TODO 7:  a JSON that is broken (for example "{") -> 400

    // GET /api/books
    // TODO 8:  with two saved books -> 200 and a JSON list of 2
    // TODO 9:  with no books -> 200 and an EMPTY list (not 404)
    // TODO 10: GET /api/books?author=Jane Austen -> only her books  (.param("author", "jane austen") works too)

    // GET /api/books/available
    // TODO 11: a book with 0 copies is not in the JSON list; the list is sorted by title

    // GET /api/books/{id}
    // TODO 12: a saved book -> 200 and its fields (id, title, author, isbn, copies)
    // TODO 13: an id that does not exist -> 404, and the error says "Book not found: 9999"
    // TODO 14: an id that is not a number ("abc") -> 400    (this one is answered by Spring itself)

    // POST /api/books/{id}/borrow     body: {"borrower": "alice"}
    // TODO 15: a book with copies -> 201, and the JSON loan has bookId, bookTitle, borrower, borrowedOn = today, returnedOn = null
    // TODO 16: ... and the book has one copy less in the DATABASE
    // TODO 17: a book with 0 copies -> 409 "No copies available"
    // TODO 18: a book that does not exist -> 404
    // TODO 19: a blank borrower, or no body at all -> 400
    // TODO 20: the 4th loan of the same person -> 409 "Loan limit reached"
}
