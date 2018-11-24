package com.transaction_history.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.transaction_history.models.Transaction;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TransactionService {
    private static TransactionService instance;
    // list of transactions in a HashMap, with accountId being the key
    // accountId -> list of transactions for that Id, sorted by date
    HashMap<String, List<Transaction>> transactions;

    private TransactionService() {
        transactions = new HashMap<>();
    }

    static public TransactionService getInstance() {
        if (instance == null) {
            instance = new TransactionService();
        }
        return instance;
    }

    public void loadTransactionsFromCSV(String csvFile) throws IOException, ParseException {
        transactions = new HashMap<>();

        Reader reader = Files.newBufferedReader(Paths.get(csvFile));
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        String[] nextRecord;
        while ((nextRecord = csvReader.readNext()) != null) {
            // trim all the values from the CSV
            for (int i=0; i < nextRecord.length; i++) nextRecord[i] = nextRecord[i].trim();

            Transaction transaction = new Transaction();
            transaction.setTransactionId(nextRecord[0]);
            transaction.setFromAccountId(nextRecord[1]);
            transaction.setToAccountId(nextRecord[2]);

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            transaction.setCreatedAt(formatter.parse(nextRecord[3]));
            transaction.setAmount(Double.parseDouble(nextRecord[4]));
            transaction.setTransactionType(nextRecord[5]);
            if (nextRecord.length > 6) {
                transaction.setRelatedTransaction(nextRecord[6]);
            }

            recordTransaction(transaction);
        }
    }

    /**
     * Each new transaction will be added as 2 transactions
     * 1 to the fromAccountId list, as a debit transaction
     * 1 to the toAccountId list, as a credit transaction
     *
     * @param transaction
     */
    public void recordTransaction(Transaction transaction) {
        try {
            if (transactions.get(transaction.getFromAccountId()) == null) {
                transactions.put(transaction.getFromAccountId(), new ArrayList<>());
            }
            if (transactions.get(transaction.getToAccountId()) == null) {
                transactions.put(transaction.getToAccountId(), new ArrayList<>());
            }

            if (transaction.getTransactionType().equals("REVERSAL")) {
                Transaction debitTransaction = getTransactionById(transaction.getFromAccountId(), transaction.getRelatedTransaction());
                Transaction creditTransaction = getTransactionById(transaction.getToAccountId(), transaction.getRelatedTransaction());

                if (debitTransaction != null) {
                    transactions.get(transaction.getFromAccountId()).remove(debitTransaction);
                }
                if (creditTransaction != null) {
                    transactions.get(transaction.getToAccountId()).remove(creditTransaction);
                }
            } else {
                transactions.get(transaction.getToAccountId()).add(transaction);
                Transaction debitTransaction = (Transaction) transaction.clone();
                debitTransaction.setAmount(-transaction.getAmount());
                transactions.get(transaction.getFromAccountId()).add(debitTransaction);
            }
        } catch (CloneNotSupportedException e) {

        }
    }

    public List<Transaction> getTransactions(String accountId, Date fromDate, Date toDate) {
        Transaction fromTransaction = new Transaction();
        fromTransaction.setCreatedAt(fromDate);
        int fromIndex = Collections.binarySearch(transactions.get(accountId), fromTransaction);

        if (fromIndex < 0) {
            // that means there is no transaction with exact date match
            // get nearest one
            fromIndex = -(fromIndex + 1);
        }

        Transaction toTransaction = new Transaction();
        toTransaction.setCreatedAt(toDate);
        int toIndex = Collections.binarySearch(transactions.get(accountId), toTransaction);
        if (toIndex < 0) {
            // that means there is no transaction with exact date match
            // get nearest one
            toIndex = -(toIndex + 1);
        }

        return transactions.get(accountId).subList(fromIndex, toIndex);
    }

    public HashMap<String, List<Transaction>> getTransactions() {
        return transactions;
    }

    private Transaction getTransactionById(String accountId, String transactionId) {
        for (Transaction transaction : transactions.get(accountId)) {
            if (transaction.getTransactionId().equals(transactionId)) {
                return transaction;
            }
        }

        return null;
    }
}
