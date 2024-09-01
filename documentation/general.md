## Worker

### CategoryDeletionWorker

X: MAXIMUM_AMOUNT_CATEGORIES = 100
Y: AMOUNT_OF_ENTRIES_TO_DELETE = 5
Z: WORK_REPEAT_INTERVAL_IN_DAYS = 30L

A worker checks the amount of entries in the database *category_table* in order to control the
amount of rows int that Room table.
The worker runs every Z days. The class CategoryDeletionWorker.kt has the following steps in its
doWork() method:

1) If there are more than X entries in the DB category_table, go to step 2, otherwise cancel here.
2) In the DB entry_table for the column 'Category' filter out all rows whose category value only
   appears once in the entire DB. Go to step 3 with these filtered rows.
3) Sort the rows by timestamp and save the last Y rows in a variable.
5) Delete the previously calculated Y entries from the category_table DB by using their category
   names.

### EntryDeletionWorker

X: MAXIMUM_AMOUNT_ENTRIES = 100
Y: AMOUNT_OF_ENTRIES_TO_DELETE = 5
Z: WORK_REPEAT_INTERVAL_IN_DAYS = 30L

1) If there are more than X entries in the DB entry_table, go to step 2, otherwise cancel here.
2) Delete the oldest Y rows by their ID field (= lowest id numbers) of DB entry_table