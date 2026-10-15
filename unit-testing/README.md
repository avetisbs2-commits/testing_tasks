# Unit Testing - Step By Step Guide

## The Idea

In the other task projects, the tests were ready and **you wrote the code**.

Here it is the opposite: **the code is finished and correct, and YOU write the tests.**

A **unit test** is a small program that checks one small piece of code (a method). It gives the method some input, and checks that the result is what you expect. If somebody changes the code later and breaks it, the test turns red and tells you at once.

Every test has 3 parts, called **Arrange - Act - Assert**:

```java
@Test
void add_twoPositiveNumbers_returnsTheSum() {
    Calculator calculator = new Calculator();      // 1. Arrange: prepare

    int result = calculator.add(2, 3);             // 2. Act: do the thing

    assertEquals(5, result);                       // 3. Assert: check the result
}
```

The name says what is tested: `method_situation_expectedResult`.

## The 7 Levels

You go from easy to hard. Each level has a class to test (in `src/main`, do not change it) and a test file (in `src/test`) where **you write the tests**.

| Level | Class to test | You practice | Test file |
|---|---|---|---|
| 1 | `Calculator` | your first tests, `assertEquals`, `assertThrows`, boundaries | `CalculatorTest` |
| 2 | `StringUtils` | edge cases (null, empty), `@ParameterizedTest` | `StringUtilsTest` |
| 3 | `BankAccount` | objects with state, `@BeforeEach`, "what must NOT change" | `BankAccountTest` |
| 4 | `PasswordValidator` | many rules, many inputs, lists, boundaries | `PasswordValidatorTest` |
| 5 | `ShoppingCart` | scenarios, collections, money, rounding | `ShoppingCartTest` |
| 6 | `UserService` | **mocks** (Mockito), `verify`, `ArgumentCaptor` | `UserServiceTest` |
| 7 | `OrderService` | the boss: 3 mocks, many paths, rollback, order of calls | `OrderServiceTest` |

Every test file starts with a comment that explains the new tools of that level, then a worked example, then a list of `TODO`s. At the beginning the TODOs say exactly what to test. At the end you only get situations and goals, and **you decide** what a good test is.

All paths are inside `src/main/java/com/example/unittesting/<level>/` and `src/test/java/com/example/unittesting/<level>/`.

## How Do I Know My Tests Are Good?

A test that is green does not mean it is a good test. A test with no `assert` is always green! So there are **two checkers**:

| Checker | The question | The idea |
|---|---|---|
| **Coverage** (JaCoCo) | Did my tests **run** all the code? | Like visiting every room of a house. |
| **Mutation testing** (PIT) | Would my tests **notice** a bug? | The tool breaks the code on purpose (`+` becomes `-`, `>` becomes `>=` ...). If all your tests stay green, you missed something. |

Coverage is the first goal. Mutation testing is the real exam.

## Step 1 - Run The Tests

In IntelliJ: click the green arrow next to a test class.

In the terminal, from this folder:

```bash
mvn test
```

To run one test class:

```bash
mvn test -Dtest=CalculatorTest
```

Right now only the worked examples exist, and they are green.

## Step 2 - Check The Coverage

```bash
mvn verify
```

It **fails** while some class is not fully covered, and tells you which one:

```text
Rule violated for class ...calculator.Calculator: lines covered ratio is 0.22, but expected minimum is 1.00
```

The goal is `1.00` (100%) for lines and branches, for every class. A *branch* is one side of an `if` (the `true` side and the `false` side).

For the details open the report in your browser:

```text
target/site/jacoco/index.html
```

Click a package, then a class. **Green** lines are run by your tests, **red** lines are not, **yellow** lines are only half covered (one side of an `if` is missing). Write the test that makes the red line green.

## Step 3 - Run The Mutation Test

Do it when the coverage is 100%:

```bash
mvn test-compile org.pitest:pitest-maven:mutationCoverage
```

Open the report:

```text
target/pit-reports/index.html
```

Find the lines marked **SURVIVED**: the tool changed the code there and **all your tests stayed green**. Read what it changed (for example "changed conditional boundary") and write a test that would fail because of that change.

The build fails below a mutation score of **90%**. Aim for **100%**.

To check one level only (faster), for example Level 1:

```bash
mvn test-compile org.pitest:pitest-maven:mutationCoverage \
  -DtargetClasses=com.example.unittesting.calculator.Calculator \
  -DtargetTests=com.example.unittesting.calculator.CalculatorTest
```

---

## Level 1 - Calculator

Open `CalculatorTest.java`. Read the worked example, then write one test for each `TODO`.

- Use `assertEquals(expected, actual)`. The **expected** value goes first.
- For `double`, add a small difference: `assertEquals(3.5, result, 0.0001)`.
- For an exception: `assertThrows(IllegalArgumentException.class, () -> calculator.divide(5, 0));`
- **Boundaries:** the most common bugs are at the edge (`>` or `>=`?). Test the value exactly on the edge. Here: zero.

## Level 2 - StringUtils

Open `StringUtilsTest.java`.

- Always ask: **what is the strangest input?** `null`, `""`, one character, big and small letters.
- Do not copy and paste the same test 5 times. Use `@ParameterizedTest` with `@CsvSource`. The worked example shows it.
- One TODO has a hint about a lowercase `a`. Mutation testing will show you why. Try it: remove that test, run PIT, and look at the survivor.

## Level 3 - BankAccount

Open `BankAccountTest.java`.

- `@BeforeEach` gives every test a **fresh** account. Tests must never depend on each other or on the order.
- Test what **does not change**: after a failed `withdraw`, the balance is the same.
- `assertAll(...)` checks several things and shows all failures, not only the first.
- To check the message of an exception: `var e = assertThrows(...); assertEquals("...", e.getMessage());`

## Level 4 - PasswordValidator

Open `PasswordValidatorTest.java`.

- Test each rule **alone**: a password that breaks exactly one rule.
- Compare the whole list: `assertEquals(List.of("..."), validator.validate("..."))`. This also checks the **order**.
- Many inputs, one test: `@ValueSource`, `@CsvSource`, `@NullAndEmptySource`.
- The length boundary: 7 characters and 8 characters.

## Level 5 - ShoppingCart

Open `ShoppingCartTest.java`. No exact tests this time, only **situations**.

- Choose numbers where a wrong calculation gives a **different** result. With `2 + 2` and `2 * 2` you cannot see the difference. With `2` and `3` you can.
- Money is in cents. The discount is rounded **down**: write the rounding test.
- After a failed operation, check that the cart is **unchanged**.

## Level 6 - UserService (mocks)

Open `UserServiceTest.java`. Read the big comment: it explains mocks.

`UserService` uses a database and an email sender. In a unit test we never use the real ones. A **mock** is a fake that you control:

```java
when(repository.findByEmail("a@b.com")).thenReturn(Optional.empty());   // I tell it what to answer
verify(emailSender).sendWelcome("a@b.com", "Anna");                      // I check it was used
verify(repository, never()).save(any());                                 // I check it was NOT used
```

- Test the **error paths** too: when the service throws, it must **not** save and **not** send an email.
- `ArgumentCaptor` lets you look inside the object that was given to a mock.

## Level 7 - OrderService (the boss)

Open `OrderServiceTest.java`. There is no setUp and no example. Read `placeOrder()` in `OrderService.java`: its comment is your specification.

- One test for **every way** the method can end.
- For each ending: which calls **must** happen, and which calls must **never** happen? ("The customer paid, but the order failed" is a real bug.)
- Test the discount **boundary** with 9999, 10000 and 10001 cents, and check the exact amount.
- `InOrder` checks the order of calls. `verifyNoMoreInteractions` checks that nothing else happened.

---

## Common Mistakes

| Problem | Reason |
|---|---|
| `assertEquals(result, 5)` shows a confusing message | The expected value goes **first**: `assertEquals(5, result)`. |
| The test is green but checks nothing | There is no `assert` (or `verify`). PIT will show it. |
| One test checks 5 different things | When it fails you do not know why. One test = one idea. (`assertAll` is fine for one idea with many parts.) |
| A test only works after another test | Tests must be independent. Use `@BeforeEach` for a fresh start. |
| The mock returns `null` or `false` and nothing works | A mock answers "empty" until you use `when(...)`. |
| `verify(...)` fails with "Wanted but not invoked" | The code did not call it, or called it with other arguments. Read the message. |
| Coverage is 100% but PIT shows survivors | The tests run the code but do not check the result well enough. Add `assert`s. |

## Rules

- Do **not** change the files in `src/main`. Only write tests.
- Do **not** use `@Disabled`, and do **not** delete tests to make the build green.
- Do **not** print with `System.out` to "check" results. Use asserts.
- Keep the tests small and readable. Somebody else must understand them.
