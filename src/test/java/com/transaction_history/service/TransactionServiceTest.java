package com.transaction_history.service;

import com.transaction_history.models.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TransactionServiceTest {
    static TransactionService transactionService;

    @BeforeClass
    static public void beforeClass()  {
        transactionService = TransactionService.getInstance();
    }

    @Test
    public void shouldLoadTransactionFromCSV() throws IOException, ParseException {
        ClassLoader classLoader = getClass().getClassLoader();
        transactionService.loadTransactionsFromCSV(classLoader.getResource("transactions.csv").getFile());

        HashMap<String, List<Transaction>> transactions = transactionService.getTransactions();
        assertEquals(2, transactions.get("ACC334455").size());
        assertEquals(1, transactions.get("ACC998877").size());
        assertEquals(3, transactions.get("ACC778899").size());
    }

    @Test
    public void shouldRecordTransaction() throws ParseException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("txn123");
        transaction.setFromAccountId("acc123");
        transaction.setToAccountId("acc456");

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        transaction.setCreatedAt(formatter.parse("20/10/2018 18:00:00"));
        transaction.setAmount(2);
        transaction.setTransactionType("PAYMENT");

        transactionService.recordTransaction(transaction);

        // assert that credit and debit transactions were recorded
        HashMap<String, List<Transaction>> transactions = transactionService.getTransactions();
        assertEquals(1, transactions.get("acc123").size());
        assertEquals(-2, transactions.get("acc123").get(0).getAmount(),0.01);
        assertEquals(1, transactions.get("acc456").size());
        assertEquals(2, transactions.get("acc456").get(0).getAmount(), 0.01);
    }

    @Test
    public void shouldRevertTransaction() throws ParseException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("txn001");
        transaction.setFromAccountId("acc123");
        transaction.setToAccountId("acc456");

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        transaction.setCreatedAt(formatter.parse("20/10/2018 18:00:00"));
        transaction.setAmount(2);
        transaction.setTransactionType("PAYMENT");

        transactionService.recordTransaction(transaction);

        Transaction revertTransaction = new Transaction();
        revertTransaction.setTransactionId("txn002");
        revertTransaction.setFromAccountId("acc123");
        revertTransaction.setToAccountId("acc456");
        revertTransaction.setCreatedAt(formatter.parse("20/10/2018 18:00:00"));
        revertTransaction.setAmount(2);
        revertTransaction.setTransactionType("REVERSAL");
        revertTransaction.setRelatedTransaction("txn001");

        transactionService.recordTransaction(revertTransaction);

        // assert that all the transactions were reverted
        HashMap<String, List<Transaction>> transactions = transactionService.getTransactions();
        assertEquals(0, transactions.get("acc123").size());
        assertEquals(0, transactions.get("acc456").size());
    }

    @Test
    public void shouldGetTransactionsByDate() throws IOException, ParseException {
        ClassLoader classLoader = getClass().getClassLoader();
        transactionService.loadTransactionsFromCSV(classLoader.getResource("transactions.csv").getFile());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date fromDate = formatter.parse("20/10/2018 12:00:00");
        Date toDate = formatter.parse("20/10/2018 19:00:00");
        List<Transaction> transactions = transactionService.getTransactions("ACC334455", fromDate, toDate);
        assertEquals(1, transactions.size());
    }
}