package com.example.shine;

public class Transaction {

    // More types can be added if needed
    enum TransactionType {
        GROCERY,
        DINING,
        RECREATION,
        BILL
    }

    private double amount;
    private TransactionType category;

    public Transaction(double amount, TransactionType category) {
        this.amount = amount;
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getCategory() {
        return category;
    }
}
