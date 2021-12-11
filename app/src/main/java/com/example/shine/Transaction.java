package com.example.shine;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private int date;
    private Recurring recurring;
    private String vendor;

    public Transaction() {}

    public Transaction(double amount, TransactionType category, Recurring recurring, int date, String vendor) {
        this.amount = amount;
        this.category = category;
        this.recurring = recurring;
        this.date = date;
        this.vendor = vendor;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getCategory() {
        return category;
    }

    public int getDate() {
        return date;
    }

    public Recurring getRecurring() {
        return recurring;
    }

    public String getVendor() {
        return vendor;
    }
}
