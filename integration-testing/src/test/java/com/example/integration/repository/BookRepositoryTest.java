package com.example.integration.repository;

import com.example.integration.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * LEVEL 1 - TEST THE REPOSITORY WITH A REAL DATABASE.
 *
 * An INTEGRATION test checks real parts working TOGETHER, with no mocks.
 * Here the parts are: your repository + Spring Data + Hibernate + a database (H2, in memory).
 *
 * The repository has NO code to read. Its queries are made from the method NAME (findByIsbn) or from
 * a JPQL text. The only way to know if they are right is to RUN them.
 *
 * @DataJpaTest   starts only the database part of the application (fast), and rolls back every test.
 *
 * Tools:
 *   repository.save(book)            INSERT
 *   repository.saveAndFlush(book)    INSERT now. "flush" sends the SQL to the database at once.
 *                                    A database rule (unique, not null) is checked ONLY when the SQL is sent,
 *                                    so to test such a rule you must flush.
 *   AssertJ:  assertThat(list).hasSize(2)
 *             assertThat(list).extracting(Book::getTitle).containsExactly("A", "B")   (this checks the ORDER)
 *             assertThatThrownBy(() -> ...).isInstanceOf(DataIntegrityViolationException.class)
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)   // use OUR database settings (needed in level 6)
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    // ---------------------------------------------------------------
    // WORKED EXAMPLE
    // ---------------------------------------------------------------
    @Test
    void save_assignsAnId_andTheBookCanBeFoundAgain() {
        Book saved = repository.save(new Book("Dune", "Frank Herbert", "111", 2));

        assertThat(saved.getId()).isNotNull();
        Optional<Book> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Dune");
    }

    // ---------------------------------------------------------------
    // YOUR TESTS
    // ---------------------------------------------------------------

    // findByIsbn
    // TODO 1: save a book, then findByIsbn with its ISBN -> the book is there (check title and author)
    // TODO 2: findByIsbn with an ISBN that does not exist -> Optional.empty()
    // TODO 3: two books with different ISBNs: findByIsbn returns the RIGHT one

    // findByAuthorIgnoreCase
    // TODO 4: two books by "Jane Austen" and one by "James Joyce": the search for "Jane Austen" returns the two
    // TODO 5: the search "jane austen" and "JANE AUSTEN" returns the same two books (upper/lower case do not matter)
    // TODO 6: a search for an author without books -> an EMPTY list (not null)
    // TODO 7: a PART of the name does not match: "Austen" finds nothing (the whole name must be equal)

    // findAvailable
    // TODO 8: a book with 0 copies is NOT in the list, a book with 1 copy IS
    // TODO 9: the list is sorted by TITLE (save "Zorro", "Alice", "Moby" in this order; expect Alice, Moby, Zorro)
    // TODO 10: when all books have 0 copies -> an empty list

    // The rules of the database
    // TODO 11: two books with the SAME isbn: the second saveAndFlush throws DataIntegrityViolationException
    // TODO 12: a book with title null: saveAndFlush throws DataIntegrityViolationException
    //          (the column is "nullable = false")

    // delete
    // TODO 13: deleteById, then findById -> Optional.empty(), and count() is 0
}
