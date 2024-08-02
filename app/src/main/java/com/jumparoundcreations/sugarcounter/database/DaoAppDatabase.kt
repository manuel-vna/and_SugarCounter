package com.jumparoundcreations.sugarcounter.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jumparoundcreations.sugarcounter.data.Category
import com.jumparoundcreations.sugarcounter.data.Entry

@Dao
interface DaoAppDatabase {

    @Insert
    fun insertEntry(vararg sugarCounter: Entry)

    //On the timeline startPoint is further to the left/in the past than endPoint
    @Query("""SELECT * FROM entry_table WHERE currentTimestamp > :startPoint AND currentTimestamp < :endPoint """)
    fun getEntries(startPoint: Long, endPoint: Long): LiveData<List<Entry>>

    @Query("""DELETE FROM entry_table WHERE id = :id""")
    fun deleteSpecificEntryRow(id: Int)

    @Query("""DELETE FROM entry_table WHERE currentTimestamp < :nowMinusTimeframe""")
    fun deleteEntriesOlderThanOneWeek(nowMinusTimeframe: Long)

    @Query("""DELETE FROM entry_table WHERE id = (SELECT MAX(id) FROM entry_table)""")
    fun deleteLastEntry()

    @Query("""SELECT * FROM entry_table WHERE category = :category ORDER BY id DESC LIMIT 1""")
    suspend fun checkIfGramValueExistsForCategory(category: String): Entry?

    @Query("""SELECT SUM(gramTotal) FROM entry_table WHERE date = :dateString""")
    fun checkIfGramThresholdIsBreached(dateString: String): Int?

    @Insert
    fun insertCategory(vararg category: Category)

    @Update
    fun updateCategory(vararg category: Category)

    @Query("""SELECT * FROM category_table""")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("""DELETE FROM category_table WHERE id = :id""")
    fun deleteSpecificCategory(id: Int)

    @Query("""DELETE FROM category_table WHERE deletionCheckbox = true""")
    fun deleteCheckedCategories()

}