package com.transaction_history.models;

public class Balance {
    double balance;
    int numberOfTransactions;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(int numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }
}
