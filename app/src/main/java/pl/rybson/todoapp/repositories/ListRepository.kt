package pl.rybson.todoapp.repositories

import pl.rybson.todoapp.data.db.TaskDao
import pl.rybson.todoapp.data.models.TaskModel
import pl.rybson.todoapp.viewmodels.SortOrder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepository @Inject constructor(private val dao: TaskDao) {

    fun getTasks(searchQuery: String, sortOrder: SortOrder) = dao.getTasks(searchQuery, sortOrder)

    suspend fun insert(task: TaskModel) = dao.insert(task)

    suspend fun delete(task: TaskModel) = dao.delete(task)

    suspend fun deleteAll() = dao.deleteAll()

}