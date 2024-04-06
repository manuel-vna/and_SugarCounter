package com.example.mva_sugarcounter.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoAppDatabase {

    @Insert
    fun insertEntry(vararg sugarCounter: Entry)

    @Query("""SELECT * FROM entry_table WHERE currentTimestamp < :startPoint AND currentTimestamp > :endPoint """)
    fun getEntries(startPoint: Long, endPoint: Long): LiveData<List<Entry>>

    @Query("""DELETE FROM entry_table WHERE id = :id""")
    fun deleteSpecificEntryRow(id: Int)

    @Query("""DELETE FROM entry_table WHERE currentTimestamp < :nowMinusTimeframe""")
    fun deleteEntriesOlderThanOneWeek(nowMinusTimeframe: Long)

    @Query("""SELECT * FROM entry_table WHERE category = :category ORDER BY id DESC LIMIT 1""")
    fun checkIfGramValueExistsForCategory(category: String): Entry?

    @Insert
    fun insertCategory(vararg category: Category)

    @Query("""SELECT * FROM category_table""")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("""DELETE FROM category_table WHERE id = :id""")
    fun deleteSpecificCategory(id: Int)


}