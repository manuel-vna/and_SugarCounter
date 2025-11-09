package com.jumparoundcreations.sugarcounter.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jumparoundcreations.sugarcounter.data.Entry
import com.jumparoundcreations.sugarcounter.data.EntryCalories
import com.jumparoundcreations.sugarcounter.data.categoryData.Category

@Dao
interface DaoAppDatabase {

    // Sugar

    @Insert
    fun insertEntry(vararg sugarCounter: Entry)

    @Query(
        "UPDATE entry_table SET " +
                "perHundredGram = :perHundredGram, " +
                "perHundredQuantity = :perHundredQuantity, gramTotal = :gramTotal " +
                "WHERE id = :id"
    )
    fun updateEntrySugarPerHundred(
        id: Int,
        perHundredGram: Int,
        perHundredQuantity: Int,
        gramTotal: Int
    )

    @Query(
        "UPDATE entry_table SET " +
                "perPieceGram= :perPieceGram, " +
                "perPieceAmount = :perPieceAmount, gramTotal = :gramTotal " +
                "WHERE id = :id"
    )
    fun updateEntrySugarPerPiece(
        id: Int,
        perPieceGram: Int,
        perPieceAmount: Int,
        gramTotal: Int
    )

    //On the timeline startPoint is further to the left/in the past than endPoint
    @Query(
        "UPDATE entry_table SET category = :newCategory WHERE category = :oldCategory AND " +
                "(currentTimestamp > :startPoint AND currentTimestamp < :endPoint)"
    )
    fun updateEntrySugarCategoryOfLastXDays(
        oldCategory: String,
        newCategory: String,
        startPoint: Long,
        endPoint: Long
    )

    //On the timeline startPoint is further to the left/in the past than endPoint
    @Query("""SELECT * FROM entry_table WHERE currentTimestamp > :startPoint AND currentTimestamp < :endPoint """)
    fun getEntries(startPoint: Long, endPoint: Long): LiveData<List<Entry>>

    @Query("""SELECT * FROM entry_table""")
    fun getAllEntries(): List<Entry>

    @Query("SELECT category FROM entry_table WHERE currentTimestamp < :deletionPointInTime")
    fun getCategoriesOfSugarEntriesToBeDeleted(deletionPointInTime: Long): List<String>

    @Query("SELECT EXISTS( SELECT 1 FROM entry_table WHERE category = :category AND currentTimestamp > :deletionPointInTime)")
    fun checkIfCategoryIsPresentSinceInSugarTable(
        category: String,
        deletionPointInTime: Long
    ): Boolean

    @Query("""DELETE FROM entry_table WHERE id = :id""")
    fun deleteSpecificEntryRow(id: Int)

    @Query("""DELETE FROM entry_table WHERE currentTimestamp < :deletionPointInTime""")
    fun deleteEntriesSugarOlderThanN(deletionPointInTime: Long)

    @Query("""DELETE FROM entry_table WHERE id = (SELECT MAX(id) FROM entry_table)""")
    fun deleteLastEntry()

    @Query("""DELETE FROM entry_table WHERE id IN (SELECT id FROM entry_table ORDER BY id ASC LIMIT :n)""")
    fun deleteOldestNEntries(n: Int)

    @Query("""SELECT * FROM entry_table WHERE category = :category ORDER BY id DESC LIMIT 1""")
    suspend fun checkIfGramValueExistsForCategory(category: String): Entry?

    @Query("""SELECT SUM(gramTotal) FROM entry_table WHERE date = :dateString""")
    fun checkIfGramThresholdIsBreached(dateString: String): Int?

    @Query(
        """
        SELECT * 
        FROM entry_table
        WHERE category IN (
            SELECT category 
            FROM entry_table
            GROUP BY category 
            HAVING COUNT(category) = 1
        )
        ORDER BY currentTimestamp ASC 
        LIMIT :amountOfEntries
    """
    )
    suspend fun getOldestUniqueEntries(amountOfEntries: Int): List<Entry>

    @Query("SELECT COUNT(*) FROM entry_table")
    suspend fun getEntryTableRowCount(): Int


    // Categories

    @Insert
    fun insertCategory(vararg category: Category)

    @Update
    fun updateCategory(vararg category: Category)

    @Query("UPDATE category_table SET category = :newCategory WHERE category = :oldCategory ")
    fun updateCategoryOnEdit(
        oldCategory: String,
        newCategory: String,
    )

    @Query("""SELECT * FROM category_table""")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("""DELETE FROM category_table WHERE id = :id""")
    fun deleteSpecificCategory(id: Int)

    @Query("""DELETE FROM category_table WHERE category = :categoryName""")
    suspend fun deleteSpecificCategoryByName(categoryName: String)

    @Query("""DELETE FROM category_table WHERE deletionCheckbox = 1""") //1 = Boolean: true
    fun deleteCheckedCategories()

    @Query("""SELECT * from category_table WHERE category = :category LIMIT 1""")
    fun getCategoryByCategoryName(category: String): Category

    @Query("""SELECT category from category_table WHERE barcodeNumber = :barcodeNumber""")
    fun getCategoryByBarcodeNumber(barcodeNumber: String): String?

    @Query("SELECT COUNT(*) FROM category_table")
    suspend fun getCategoryTableRowCount(): Int

    // Calories

    @Insert
    fun insertEntryCalories(vararg caloriesEntry: EntryCalories)

    @Query(
        "UPDATE calories_table SET " +
                "caloriesPerPiece = :caloriesPerPiece, " +
                "caloriesAmount = :caloriesAmount, caloriesTotal = :caloriesTotal" +
                " WHERE id = :id"
    )
    fun updateEntryCalories(
        id: Int,
        caloriesPerPiece: Int,
        caloriesAmount: Int,
        caloriesTotal: Int
    )

    //On the timeline startPoint is further to the left/in the past than endPoint
    @Query(
        "UPDATE calories_table SET category = :newCategory WHERE category = :oldCategory AND " +
                "(currentTimestamp > :startPoint AND currentTimestamp < :endPoint)"
    )
    fun updateEntryCaloriesCategoryOfLastXDays(
        oldCategory: String,
        newCategory: String,
        startPoint: Long,
        endPoint: Long
    )

    //On the timeline startPoint is further to the left/in the past than endPoint
    @Query("""SELECT * FROM calories_table WHERE currentTimestamp > :startPoint AND currentTimestamp < :endPoint """)
    fun getEntryCalories(startPoint: Long, endPoint: Long): LiveData<List<EntryCalories>>

    @Query("""SELECT * FROM calories_table""")
    fun getAllEntriesCalories(): List<EntryCalories>

    @Query("SELECT category FROM calories_table WHERE currentTimestamp < :deletionPointInTime")
    fun getCategoriesOfCaloriesEntriesToBeDeleted(deletionPointInTime: Long): List<String>

    @Query("SELECT EXISTS( SELECT 1 FROM calories_table WHERE category = :category AND currentTimestamp > :deletionPointInTime)")
    fun checkIfCategoryIsPresentSinceInCaloriesTable(
        category: String,
        deletionPointInTime: Long
    ): Boolean

    @Query("""DELETE FROM calories_table WHERE id = :id""")
    fun deleteSpecificEntryCaloriesRow(id: Int)

    @Query("""SELECT SUM(caloriesTotal) FROM calories_table WHERE date = :dateString""")
    fun checkIfCaloriesThresholdIsBreached(dateString: String): Int?

    @Query("""DELETE FROM calories_table WHERE id = (SELECT MAX(id) FROM calories_table)""")
    fun deleteLastEntryCalories()

    @Query("""DELETE FROM calories_table WHERE currentTimestamp < :deletionPointInTime""")
    fun deleteEntriesCaloriesOlderThanN(deletionPointInTime: Long)

    @Query("""SELECT * FROM calories_table WHERE category = :category ORDER BY id DESC LIMIT 1""")
    suspend fun checkIfCaloriesValueExistsForCategory(category: String): EntryCalories?
}