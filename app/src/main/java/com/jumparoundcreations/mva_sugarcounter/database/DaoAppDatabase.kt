package com.jumparoundcreations.mva_sugarcounter.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jumparoundcreations.mva_sugarcounter.data.Category
import com.jumparoundcreations.mva_sugarcounter.data.Entry

@Dao
interface DaoAppDatabase {

    // Entries

    @Insert
    fun insertEntry(vararg sugarCounter: Entry)

    //On the timeline startPoint is further to the left/in the past than endPoint
    @Query("""SELECT * FROM entry_table WHERE currentTimestamp > :startPoint AND currentTimestamp < :endPoint """)
    fun getEntries(startPoint: Long, endPoint: Long): LiveData<List<Entry>>

    @Query("""SELECT * FROM entry_table""")
    fun getAllEntries(): List<Entry>

    @Query("""DELETE FROM entry_table WHERE id = :id""")
    fun deleteSpecificEntryRow(id: Int)

    @Query("""DELETE FROM entry_table WHERE currentTimestamp < :nowMinusTimeframe""")
    fun deleteEntriesOlderThanOneWeek(nowMinusTimeframe: Long)

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

    @Query("""SELECT * FROM category_table""")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("""DELETE FROM category_table WHERE id = :id""")
    fun deleteSpecificCategory(id: Int)

    @Query("""DELETE FROM category_table WHERE category = :categoryName""")
    suspend fun deleteSpecificCategoryByName(categoryName: String)

    @Query("""DELETE FROM category_table WHERE deletionCheckbox = true""")
    fun deleteCheckedCategories()

    @Query("""SELECT * from category_table WHERE category = :category LIMIT 1""")
    fun getCategoryByCategoryName(category: String): Category

    @Query("""SELECT category from category_table WHERE barcodeNumber = :barcodeNumber""")
    fun getCategoryByBarcodeNumber(barcodeNumber: String): String?

    @Query("SELECT COUNT(*) FROM category_table")
    suspend fun getCategoryTableRowCount(): Int

}