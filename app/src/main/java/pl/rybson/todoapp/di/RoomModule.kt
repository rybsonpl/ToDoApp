package pl.rybson.todoapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import pl.rybson.todoapp.data.db.TaskDatabase
import pl.rybson.todoapp.utils.Constants
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun provideTaskDatabase(@ApplicationContext context: Context, callback: TaskDatabase.Callback) =
        Room.databaseBuilder(context, TaskDatabase::class.java, Constants.TASK_DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

    @Singleton
    @Provides
    fun provideTaskDao(database: TaskDatabase) = database.taskDao()
}