## Worker

### DeletionWorker

X: MAXIMUM_AMOUNT_ENTRIES = 100
Y: AMOUNT_OF_ENTRIES_TO_DELETE = 5
Z: WORK_REPEAT_INTERVAL_IN_DAYS = 30L

1) If there are more than X entries in the DB entry_table, go to step 2, otherwise cancel here.
2) Delete the oldest Y rows by their ID field (= lowest id numbers) of DB entry_table

### DeletionWorkerTest