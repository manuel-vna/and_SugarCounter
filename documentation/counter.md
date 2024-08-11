## Scanning a barcode

### Userflow/Appflow 1:

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

-> App: Saving the barcode starts Appflow 3

### Userflow 2:

App: Shows the user a Card that the scanned barcode is not connected to a category yet but it can be
connected by adding a category and a gram data.
User Option 1: Can close the Card.
User Option 2: Can use the barcode to combine it with a category.
User Option 3: Can close the app, which will remove the currently saved barcode.

### Appflow 3:

App: Saves a category
Saving option 1: The category is not in the database yet and there is NO barcode displayed to the
user: Save the category only
Saving option 2: The category is not in the database yet and a barcode is displayed to the user:
Save the category and the barcode in a new row
Saving option 3: The category is already in the database and the user is displayed a barcode: Get
that category from the database and save the barcode with it