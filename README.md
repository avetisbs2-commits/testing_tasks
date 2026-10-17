# Testing Tasks

This project contains separate Java practice modules about **software testing**.

In the other task projects you write the code. Here the code is finished and **you write the tests**.

Each module is an independent Maven module with its own `README.md` guide.

The modules will grow from simple to harder topics:

```text
unit testing          (JUnit 5, assertions, parameterized tests, mocks with Mockito)
integration testing   (Spring Boot tests, a real database, REST with MockMvc)
test-driven development (TDD)
```

## Project Structure

```text
testing_tasks/
├── README.md
├── pom.xml
├── .gitignore
├── unit-testing/
│   ├── README.md
│   ├── pom.xml
│   └── src/
│       ├── main/   (finished code to test - do not change)
│       └── test/   (YOUR tests)
└── integration-testing/
    ├── README.md
    ├── pom.xml
    └── src/
        ├── main/   (a small Library application - do not change, except one bug to fix)
        └── test/   (YOUR tests)
```

## Current Modules

```text
unit-testing          - 7 levels, from a Calculator to an OrderService with mocks
integration-testing   - 5 levels + 1 bonus: a real database, REST API, a bug hunt, test data, PostgreSQL
```

## What You Need

- JDK 21 or newer
- Maven (or the Maven in IntelliJ IDEA)

Nothing else to install. `integration-testing` uses Spring Boot and an in-memory H2 database (both come with Maven). A real PostgreSQL is needed only for the optional bonus level 6.

## How Your Work Is Checked

| Command | What it does |
|---|---|
| `mvn test` | runs your tests |
| `mvn verify` | runs your tests and **fails** if some code is not 100% covered (JaCoCo) |
| `mvn test-compile org.pitest:pitest-maven:mutationCoverage` | breaks the code on purpose and checks that your tests **notice** (PIT mutation testing) |

Run them from the module folder, or from the root with `-pl <module>` (for example `-pl integration-testing`).

## Notes

- Each module has a step-by-step guide in its own `README.md`.
- The modules are separate and do not share code.
