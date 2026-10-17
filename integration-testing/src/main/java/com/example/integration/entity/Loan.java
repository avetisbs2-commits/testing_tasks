package com.example.integration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

/** One book lent to one person. While returnedOn is null, the book is still out. PROVIDED CODE - do not change. */
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Book book;

    @Column(nullable = false)
    private String borrower;

    @Column(nullable = false)
    private LocalDate borrowedOn;

    private LocalDate returnedOn;

    protected Loan() {
    }

    public Loan(Book book, String borrower, LocalDate borrowedOn) {
        this.book = book;
        this.borrower = borrower;
        this.borrowedOn = borrowedOn;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public String getBorrower() {
        return borrower;
    }

    public LocalDate getBorrowedOn() {
        return borrowedOn;
    }

    public LocalDate getReturnedOn() {
        return returnedOn;
    }

    public void setReturnedOn(LocalDate returnedOn) {
        this.returnedOn = returnedOn;
    }
}
