## Scanning a barcode

### Userflow 1:

User: Scans barcode
App:
If barcode isEmpty: Flow stops.
If barcode is not empty: Flow goes on.
App: Checks if the scanned barcode can be found in the category_table and if a corresponding
category can be returned
If no:
Above the category field a Card is shown that informs the user that the given barcode is not in the
database yet.
User: The user can fill out the fields and press Save which automatically combines the barcode with
a category.
If yes:
The category field is filled with the returned category and the entry data is retrieved for this
category.
If there is gram data:
User: Can save the entry with the retrieved data directly or edit the data and save it then.
No gram data returned:
App: Simply leaves the two sugar gram fields untouched and let's the user enter values.

### Userflow 2:
