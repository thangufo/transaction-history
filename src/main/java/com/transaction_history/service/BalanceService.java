package com.transaction_history.service;

import com.transaction_history.models.Balance;
import com.transaction_history.models.Transaction;

import java.util.Date;
import java.util.List;

public class BalanceService {
    private static BalanceService instance;
    private TransactionService transactionService;

    private BalanceService() {
        transactionService = TransactionService.getInstance();
    }

    static public BalanceService getInstance() {
        if (instance == null) {
            instance = new BalanceService();
        }
        return instance;
    }

    public Balance getBalance(String accountId, Date fromDate, Date toDate) {
        List<Transaction> transactions = transactionService.getTransactions(accountId, fromDate, toDate);
        Balance balance = new Balance();

        balance.setBalance(transactions.stream().mapToDouble(transaction -> transaction.getAmount()).sum());
        balance.setNumberOfTransactions(transactions.size());

        return balance;
    }
}
