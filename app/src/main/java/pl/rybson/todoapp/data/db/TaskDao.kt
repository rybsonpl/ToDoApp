package pl.rybson.todoapp.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.rybson.todoapp.data.models.TaskModel
import pl.rybson.todoapp.viewmodels.SortOrder

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskModel)

    @Update
    suspend fun update(task: TaskModel)

    @Delete
    suspend fun delete(task: TaskModel)

    @Query("DELETE FROM tasks_table")
    suspend fun deleteAll()


    @Query("SELECT * FROM tasks_table WHERE title LIKE '%' || :searchQuery || '%' ORDER BY date DESC")
    fun getTasksSortedByLatest(searchQuery: String): Flow<List<TaskModel>>

    @Query("SELECT * FROM tasks_table WHERE title LIKE '%' || :searchQuery || '%' ORDER BY date ASC")
    fun getTasksSortedByOldest(searchQuery: String): Flow<List<TaskModel>>

    @Query("SELECT * FROM tasks_table WHERE title LIKE '%' || :searchQuery || '%' ORDER BY CASE WHEN priority LIKE 'H%' THEN 1 WHEN priority LIKE '%M' THEN 2 WHEN priority LIKE 'L%' THEN 3 END")
    fun getTasksSortedByHighPriority(searchQuery: String): Flow<List<TaskModel>>

    @Query("SELECT * FROM tasks_table WHERE title LIKE '%' || :searchQuery || '%' ORDER BY CASE WHEN priority LIKE 'L%' THEN 1 WHEN priority LIKE '%M' THEN 2 WHEN priority LIKE 'H%' THEN 3 END")
    fun getTasksSortedByLowPriority(searchQuery: String): Flow<List<TaskModel>>

    fun getTasks(searchQuery: String, sortOrder: SortOrder): Flow<List<TaskModel>> =
        when(sortOrder) {
            SortOrder.BY_LATEST -> getTasksSortedByLatest(searchQuery)
            SortOrder.BY_OLDEST -> getTasksSortedByOldest(searchQuery)
            SortOrder.BY_HIGH_PRIORITY -> getTasksSortedByHighPriority(searchQuery)
            SortOrder.BY_LOW_PRIORITY -> getTasksSortedByLowPriority(searchQuery)
        }
}