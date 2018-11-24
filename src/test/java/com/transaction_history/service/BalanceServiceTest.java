package com.transaction_history.service;

import com.transaction_history.models.Balance;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class BalanceServiceTest {
    static TransactionService transactionService;
    static BalanceService balanceService;

    @BeforeClass
    static public void beforeClass()  {
        transactionService = TransactionService.getInstance();
        balanceService = BalanceService.getInstance();
    }

    @Test
    public void getBalance() throws IOException, ParseException {
        ClassLoader classLoader = getClass().getClassLoader();
        transactionService.loadTransactionsFromCSV(classLoader.getResource("transactions.csv").getFile());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date fromDate = formatter.parse("20/10/2018 12:00:00");
        Date toDate = formatter.parse("20/10/2018 19:00:00");
        Balance balance = balanceService.getBalance("ACC334455", fromDate, toDate);
        assertEquals(1, balance.getNumberOfTransactions());
        assertEquals(-25, balance.getBalance(),0.01);
    }
}