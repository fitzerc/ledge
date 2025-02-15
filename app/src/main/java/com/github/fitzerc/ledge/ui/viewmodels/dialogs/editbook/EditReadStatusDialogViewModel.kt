package com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.ReadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditReadStatusDialogViewModel(ledgeDb: LedgeDatabase): ViewModel() {
    private val _readStatuses = MutableStateFlow<List<ReadStatus>>(emptyList())
    val readStatuses: StateFlow<List<ReadStatus>> = _readStatuses.asStateFlow()

    init {
        viewModelScope.launch {
            ledgeDb.readStatusDao().getReadStatuses().collect() { statuses ->
                _readStatuses.value = statuses
            }
        }
    }
}

class EditReadStatusDialogViewModelFactory(
    private val ledgeDb: LedgeDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditReadStatusDialogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditReadStatusDialogViewModel(ledgeDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
