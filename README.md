### Assumptions
- The transactions will be provided using a CSV file
- The CSV header is: transactionId, fromAccountId, toAccountId, createdAt, amount, transactionType, relatedTransaction
- All the data in the CSV file is correct, and sorted by date in ascending order.

### Solution
- The business logic are in 2 singleton services: TransactionService and BalanceService
The following data structure is used for storing the transactions
`HashMap<String, List<Transaction>> transactions`

The key of the hashmap is the account id.
When a transaction is processed, 2 transactions will be added into the transaction list
- Debit transaction into the From Account
- Credit transaction into the destination account

When searching the transactions base on the date, Collections.binarySearch() is used, since
the list is already ordered. 
- Once we have a list of transactions of an account between a certain date range, just sum all of the amounts
to get the relative balance.

### Build and run
```
./gradlew build
java -jar ./build/libs/transaction-history.jar 'transactions.csv' 'ACC334455' '20/10/2018 12:00:00' '20/10/2018 19:00:00'
```

### Run using gradle
```
./gradlew run -PappArgs="['transactions.csv', 'ACC334455', '20/10/2018 12:00:00', '20/10/2018 19:00:00']"
```

### Run test using gradle
```gradle test```