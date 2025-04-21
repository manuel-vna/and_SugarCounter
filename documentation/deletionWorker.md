## DeletionWorker

CustomWorkerFactory.kt:
What
An implementation of the abstract class WorkerFactory whose method 'createWorker()' is invoked
every time a work runs.
Why
In order to make the DeletionWorker testable, a CustomWorkFactory makes it possible to hand in
the DAO and MainSharedPreferences as parameters into the DeletionWorker.kt

DeletionWorker.kt:

The property 'automaticDeletionValueMapping' maps the options of a Switch with which the user can
choose deletion
periods to a time span represented in seconds. This time span is reduced of the current timestamp
to find out which entries need to be deleted when the worker runs.

dowWork():

1) Get all categories of sugar entries that will be deleted in this run
2) Check if this category is present in one of the entries that are not deleted an map to Boolean
   result into a List<Pair(category: String, result: Boolean)>.
3) Filter this list by all Pairs whose result is false and map the resulting Pairs to a list of
   categories
4) Do the same 3 steps for Calories
5) Combine the categories that need to be deleted from Sugar entries with those who need to be
   deleted from Calories entries
6) Delete those categories
7) Delete the sugar entries that are older than the set timestamp
8) Delete the calories entries that are older than the set timestamp



