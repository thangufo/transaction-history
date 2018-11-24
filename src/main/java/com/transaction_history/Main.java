package com.transaction_history;

import com.transaction_history.models.Balance;
import com.transaction_history.service.BalanceService;
import com.transaction_history.service.TransactionService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.exit;

public class Main {
    static TransactionService transactionService;
    static BalanceService balanceService;

    public static void main(String[] args ) {
        if (args.length < 4) {
            System.out.println("Command line syntax: java transaction-history.jar <csv filename> <accountid> <from date> <to date>");
            exit(1);
        }

        transactionService = TransactionService.getInstance();
        balanceService = BalanceService.getInstance();

        try {
            transactionService.loadTransactionsFromCSV(args[0]);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date fromDate = formatter.parse(args[2]);
            Date toDate = formatter.parse(args[3]);
            Balance balance = balanceService.getBalance(args[1], fromDate, toDate);

            System.out.printf("Relative balance for the period is: %.2f\n", balance.getBalance());
            System.out.printf("Number of transactions included is: %d\n", balance.getNumberOfTransactions());
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Invalid date format");
        }
    }
}
