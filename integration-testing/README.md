# Integration Testing - Step By Step Guide

## The Idea

In the `unit-testing` module you tested **one class alone**. Everything around it was replaced by mocks (fake objects).

An **integration test** is the opposite: it checks that **real parts work together**, with **no mocks**.

```text
UNIT test:         Service  ←→  [fake repository]  [fake email sender]

INTEGRATION test:  HTTP request → Controller → Service → Repository → real database
                   (all of it runs for real)
```

Why do we need both? Because unit tests with mocks can be all green while the application is broken. A mock says "the save works". It cannot know that the real database refuses the data, that a query is wrong, that a transaction is missing, or that a JSON field has a wrong name. Only a real run can tell.

| | Unit test | Integration test |
|---|---|---|
| What is tested | one class | several parts together |
| Dependencies | mocks | real (Spring, Hibernate, a database) |
| Speed | very fast | slower (the application starts) |
| Finds | logic bugs | wiring bugs, SQL bugs, transaction bugs, JSON bugs |

## The Application You Test

A small **Library**: books, and loans of books to people. It is a Spring Boot application with a REST API, Hibernate (JPA), and an in-memory H2 database. **It is finished** (with one exception, see level 4). You only write tests.

The rules:

- A book has `title`, `author`, a unique `isbn`, and `copies` on the shelf.
- When somebody **borrows** a book, there is one copy less and a `Loan` is created.
- A person can have at most **3** active loans (a loan is active until it is returned).
- **Returning** a loan gives the copy back. A loan can be returned only once.
- `borrow-many` lends several books at once: **all or nothing**.

The API:

| Request | Answer |
|---|---|
| `POST /api/books` | 201 + the book, or 400 (bad data), 409 (ISBN exists) |
| `GET /api/books` (and `?author=...`) | 200 + a list |
| `GET /api/books/available` | 200 + books with a copy on the shelf, sorted by title |
| `GET /api/books/{id}` | 200, or 404 |
| `POST /api/books/{id}/borrow` body `{"borrower": "alice"}` | 201 + the loan, or 400, 404, 409 |
| `POST /api/borrow-many` body `{"bookIds": [1,2], "borrower": "alice"}` | 201 + the loans, or 400, 404, 409 |
| `POST /api/loans/{id}/return` | 200 + the loan, or 404, 409 |
| `GET /api/loans?borrower=alice` | 200 + the active loans of alice |

Want to play with it by hand? Run `mvn spring-boot:run` and call it with a browser or `curl`.

## The 6 Levels

| Level | Topic | Test file | You practice |
|---|---|---|---|
| 1 | The repository on a real database | `BookRepositoryTest`, `LoanRepositoryTest` | `@DataJpaTest`, queries, database rules, `flush` |
| 2 | Service + repositories + database | `LibraryServiceTest` | `@SpringBootTest`, checking the **database** after a call |
| 3 | The REST API | `BookControllerTest`, `LoanControllerTest` | `MockMvc`, status codes, JSON |
| 4 | Whole stories, and a **bug hunt** | `LibraryFlowTest` | tests without a transaction, finding a real bug |
| 5 | Test data and isolation | `TestDataTest` | `@Sql`, random test order, cleaning up |
| 6 (bonus) | A real PostgreSQL | (all tests) | the same tests on another database |

Every test file starts with a comment about the new tools, then a worked example, then the `TODO` list. In the first levels the TODOs say exactly what to test. Later you decide more by yourself.

All test files are in `src/test/java/com/example/integration/`. **Do not change `src/main`** (except to fix the bug of level 4).

## How Do I Know My Tests Are Good?

The same two checkers as in `unit-testing`:

| Checker | The question |
|---|---|
| **Coverage** (JaCoCo) | Did my tests **run** all the code of the application? |
| **Mutation testing** (PIT) | Would my tests **notice** a bug? The tool breaks the code on purpose and checks that a test turns red. |

```bash
mvn verify                                                   # coverage: must be 100%
mvn test-compile org.pitest:pitest-maven:mutationCoverage    # mutation: must be 90% or more, aim for 100%
```

Reports: `target/site/jacoco/index.html` and `target/pit-reports/index.html`.

> **Limit to know:** a repository interface has no code of its own, so the checkers cannot see level 1. Nobody will force you to write those tests, so write every `TODO` of level 1: these tests find the SQL bugs that nothing else finds.

## Step 1 - Run The Tests

In IntelliJ: click the green arrow next to a test class.

In the terminal, from this folder:

```bash
mvn test
mvn test -Dtest=BookRepositoryTest       # one class only
```

The worked examples are green. The application starts for every kind of test, so be patient: the first run is slower.

---

## Level 1 - The Repository

Open `BookRepositoryTest` and `LoanRepositoryTest`.

The repository has **no code to read**. Its queries are made from the **method name** (`findByIsbn`, `findByAuthorIgnoreCase`) or from a JPQL text (`findAvailable`). The only way to know that they are right is to **run them** on a database.

- `@DataJpaTest` starts only the database part. Every test is rolled back, so each test starts with an empty database.
- A rule of the database (`unique`, `not null`) is checked only when the SQL is **sent**. Use `saveAndFlush(...)` to send it now, then check the exception.
- To check **sorting**, use `extracting(...).containsExactly(...)`. To check a list without caring about the order, use `containsExactlyInAnyOrder(...)`.
- A `Loan` needs a `Book`. Save the book first.

## Level 2 - The Service

Open `LibraryServiceTest`.

The service is tested with **real** repositories. So a test can ask the database what happened. A good test of `borrow` does not only look at the returned `Loan`. It also reloads the book and checks that the copies went down.

- Check three things: **the result**, **the database after it**, and when it fails, **that the database is unchanged**.
- Test the **boundary** of the loan limit: the 3rd loan is allowed, the 4th is not.
- `@Transactional` on the test class rolls back every test. Easy cleanup, but remember it for level 4.

## Level 3 - The REST API

Open `BookControllerTest` and `LoanControllerTest`.

`MockMvc` sends an HTTP request into the application without a network. You check the **status code** and the **JSON**.

```java
mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
       .andExpect(status().isCreated())
       .andExpect(jsonPath("$.title").value("Dune"));
```

- Despite the name, `MockMvc` has **nothing to do with mocks**. Everything behind it is real.
- Prepare the data with the **repositories**: faster and shorter than many POST requests.
- Test the bad requests too: a blank field, a negative number, a text that is not a number in the URL, a broken JSON. Each must answer **400**, not 500.
- A `@ParameterizedTest` helps when only the JSON changes.

## Level 4 - Stories And A Bug Hunt

Open `LibraryFlowTest`.

First, the stories: a test with many steps (add a book, borrow it, look at the shelf, return it). The bugs live **between** the steps.

This class has **no `@Transactional`**, and that is on purpose. A transaction is "all or nothing". If the test itself runs inside a transaction, it hides what the application does with its **own** transactions: nothing is ever really committed. To see what the application really saves or does not save, the test must stand outside. The price: you clean the database yourself (the provided `cleanDatabase` method does it).

**The bug hunt.** `POST /api/borrow-many` must be all or nothing. Write the tests of TODO 4, 5 and 6, and run them.

They are **red**. You found a real bug in the application, and the unit tests with mocks could never find it.

How to find it:

1. Read the failure message. Which number is wrong? What does it tell you was saved?
2. Open `LibraryService`. Compare `borrow` and `borrowMany`. Look at the annotations above them.
3. A `@Transactional` method is "all or nothing". What happens to the first book when the second one fails, if there is **no** transaction around both?

Fix the bug in `src/main`, and run the tests again. Green!

## Level 5 - Test Data And Isolation

Open `TestDataTest` and the file `src/test/resources/sql/library-data.sql`.

`@Sql` runs an SQL script before a test. It is the clean way to prepare a **bigger** set of data.

The problem of this level is **isolation**. The tests run in a **random order** (see `junit-platform.properties`). Add a second test and run the class several times. It fails: the data of the first test is still there. Read the error, then solve it. Two good ways:

- **A.** A cleanup script after every test: write `cleanup.sql` (careful: a loan points to a book, so the **order** of the `delete` lines matters), and use `@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)`.
- **B.** `@Transactional` on the class. The script and the test run in one transaction that is rolled back.

Rule for every test, in every class: **leave the database as you found it.**

## Level 6 (bonus) - A Real PostgreSQL

H2 is fast and needs no setup, but it is **not** PostgreSQL. Sometimes a test passes on H2 and fails on the real database. Let us find out.

1. Make sure PostgreSQL runs on your computer, and create an **empty** test database:

```sql
CREATE DATABASE library_test;
```

2. If your user or password is not `postgres` / `postgres`, change them in `src/main/resources/application-postgres.properties`.

3. Run **all** your tests on PostgreSQL:

```bash
mvn test -Ppostgres
```

> Use an empty test database. Hibernate creates the tables `books` and `loans` there, and **drops** them at the end.

4. Compare with the H2 result. Do all tests still pass? If one fails, why? Things that often differ between databases:
   - The **order** of rows when there is no `ORDER BY`. H2 usually returns the rows in the order they were saved. PostgreSQL may not, especially after an `UPDATE`. A test that expects an order from `findAll()` can pass on H2 and fail on PostgreSQL. (How do you fix that? Sort in the query, or do not test the order.)
   - The text of an error message, or the exact exception type.

The answer to "which tests failed, and why" is your result for this level. If nothing fails, you wrote independent tests. Good job.

---

## Common Mistakes

| Problem | Reason |
|---|---|
| The unique-ISBN test passes but should not | You forgot `saveAndFlush`. The database rule was never checked. |
| A test only works when it runs after another test | The tests share data. Clean up, or use `@Transactional`. |
| `LazyInitializationException` or a missing row | You read data after the transaction ended. Reload it with the repository. |
| `MockMvc` test says 400 instead of 201 | The JSON has a wrong field name, or a field is blank. Print the response with `.andDo(print())`. |
| The rollback test is green on the buggy code | The test class has `@Transactional`. Remove it from that class. |
| Coverage is 100% but PIT shows survivors | The tests run the code but do not check the result. Add asserts on the **status**, the **JSON** and the **database**. |
| Everything passes on H2, fails on PostgreSQL | A test depended on H2 behavior (often the order of rows). |

## Rules

- Do **not** change `src/main`, except to fix the bug of level 4.
- Do **not** use mocks (`@MockBean`, `mock(...)`) here. The point is that everything is real.
- Do **not** use `@Disabled`, and do not delete tests to make the build green.
- Every test must work alone and in any order.
