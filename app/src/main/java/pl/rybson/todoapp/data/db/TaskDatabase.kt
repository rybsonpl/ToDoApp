package pl.rybson.todoapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.rybson.todoapp.R
import pl.rybson.todoapp.di.ApplicationScope
import pl.rybson.todoapp.data.models.Priority
import pl.rybson.todoapp.data.models.TaskModel
import pl.rybson.todoapp.data.db.TaskDao
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [TaskModel::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope,
        @ApplicationContext private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.run {
                    insert(TaskModel(title = context.getString(R.string.add), priority = Priority.HIGH, description = context.getString(R.string.click_button_and_complete_the_form)))
                    insert(TaskModel(title = context.getString(R.string.delete), priority = Priority.MEDIUM, description = context.getString(R.string.swipe_to_delete)))
                    insert(TaskModel(title = context.getString(R.string.sample_note), priority = Priority.LOW, description = context.getString(R.string.lorem_ipsum)))
                }
            }
        }
    }
}