package pl.rybson.todoapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tasks_table")
@TypeConverters(Converters::class)
data class TaskModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String = "",
    var priority: Priority = Priority.MEDIUM,
    var description: String? = null,
    val date: Long = System.currentTimeMillis()
) : Parcelable

class Converters {
    @TypeConverter
    fun priorityToString(priority: Priority) = priority.name

    @TypeConverter
    fun stringToPriority(string: String) = enumValueOf<Priority>(string)
}

enum class Priority {
    HIGH,
    MEDIUM,
    LOW
}