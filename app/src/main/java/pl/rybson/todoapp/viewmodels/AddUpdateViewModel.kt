package pl.rybson.todoapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.rybson.todoapp.data.models.TaskModel
import pl.rybson.todoapp.repositories.AddUpdateRepository

class AddUpdateViewModel @ViewModelInject constructor(private val repository: AddUpdateRepository) : ViewModel() {

    fun insert(task: TaskModel) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: TaskModel) = viewModelScope.launch {
        repository.update(task)
    }

}