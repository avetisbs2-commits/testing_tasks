package com.example.integration.repository;

import com.example.integration.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Talks to the "books" table. You write no SQL for the first two methods:
 * Spring builds the query from the METHOD NAME. The third one has a hand-written JPQL query.
 * A repository has no code to "cover", so the only way to test it is to run it on a real (in-memory) database.
 *
 * PROVIDED CODE - do not change.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    /** All books of that author. Upper and lower case do not matter. */
    List<Book> findByAuthorIgnoreCase(String author);

    /** Only the books that have at least one copy on the shelf, sorted by title. */
    @Query("select b from Book b where b.copies > 0 order by b.title")
    List<Book> findAvailable();
}
