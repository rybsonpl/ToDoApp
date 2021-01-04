package pl.rybson.todoapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import pl.rybson.todoapp.data.models.TaskModel
import pl.rybson.todoapp.repositories.ListRepository
import pl.rybson.todoapp.repositories.PreferencesRepository

@ExperimentalCoroutinesApi
class ListViewModel @ViewModelInject constructor(
    private val repository: ListRepository,
    private val preferences: PreferencesRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    private val sortOrderFlow = preferences.sortOrderFlow

    val emptyList = MutableStateFlow(false)

    private val taskFlow = combine(searchQuery, sortOrderFlow) { searchQuery, sortOrder ->
        Pair(searchQuery, sortOrder)
    }.flatMapLatest { (searchQuery, sortOrder) ->
        repository.getTasks(searchQuery, enumValueOf(sortOrder))
    }

    val tasks = taskFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferences.updateSortOrder(sortOrder)
    }

    fun deleteAllTasks() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun delete(task: TaskModel) = viewModelScope.launch {
        repository.delete(task)
    }

    fun insert(task: TaskModel) = viewModelScope.launch {
        repository.insert(task)
    }

    // SettingsBottomSheetFragment

    val layoutManagerTypeFlow = preferences.layoutManagerTypeFlow

    fun onLayoutManagerSelected() = viewModelScope.launch {
        preferences.updateLayoutManagerType()
    }
}

enum class SortOrder {
    BY_LATEST,
    BY_OLDEST,
    BY_HIGH_PRIORITY,
    BY_LOW_PRIORITY
}

enum class LayoutManagerType {
    LINEAR,
    STAGGERED
}