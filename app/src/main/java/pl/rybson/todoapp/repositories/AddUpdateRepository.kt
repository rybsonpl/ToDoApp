package pl.rybson.todoapp.repositories

import pl.rybson.todoapp.data.db.TaskDao
import pl.rybson.todoapp.data.models.TaskModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddUpdateRepository @Inject constructor(private val dao: TaskDao) {

    suspend fun insert(task: TaskModel) = dao.insert(task)

    suspend fun update(task: TaskModel) = dao.update(task)
}