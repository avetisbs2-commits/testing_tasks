-- Test data for level 5. It is loaded with @Sql before a test.
-- Books:  title, author, isbn, copies on the shelf
insert into books (title, author, isbn, copies) values ('Dune', 'Frank Herbert', '111', 2);
insert into books (title, author, isbn, copies) values ('Emma', 'Jane Austen', '222', 0);
insert into books (title, author, isbn, copies) values ('Persuasion', 'Jane Austen', '333', 1);
insert into books (title, author, isbn, copies) values ('The Hobbit', 'J.R.R. Tolkien', '444', 5);
insert into books (title, author, isbn, copies) values ('Ulysses', 'James Joyce', '555', 1);

-- Loans:  alice has 3 books out (the maximum). bob returned his book, so he has no active loans.
insert into loans (book_id, borrower, borrowed_on, returned_on)
    values ((select id from books where isbn = '111'), 'alice', '2026-01-10', null);
insert into loans (book_id, borrower, borrowed_on, returned_on)
    values ((select id from books where isbn = '333'), 'alice', '2026-01-11', null);
insert into loans (book_id, borrower, borrowed_on, returned_on)
    values ((select id from books where isbn = '444'), 'alice', '2026-01-12', null);
insert into loans (book_id, borrower, borrowed_on, returned_on)
    values ((select id from books where isbn = '222'), 'bob', '2026-01-05', '2026-01-20');
