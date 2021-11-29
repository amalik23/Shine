package com.example.shine;

import java.time.LocalDate;

public class Transaction {

    // More types can be added if needed
    enum TransactionType {
        GROCERY,
        DINING,
        RECREATION,
        BILL
    }

    // If the payment is recurring then we can also store that data and automatically apply it
    enum Recurring {
        SINGLE,
        WEEKLY,
        MONTHLY,
        ANNUALLY
    }

    private double amount;
    private TransactionType category;
    private LocalDate date;
    private Recurring recurring;

    public Transaction() {}

    public Transaction(double amount, TransactionType category, Recurring recurring, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.recurring = recurring;
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public Recurring getRecurring() {
        return recurring;
    }
}
