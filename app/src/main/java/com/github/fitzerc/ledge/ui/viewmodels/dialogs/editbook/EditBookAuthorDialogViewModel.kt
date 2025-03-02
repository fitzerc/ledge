package com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditBookAuthorDialogViewModel(private val ledgeDb: LedgeDatabase): ViewModel() {
    private val _autoCompAuthorsNames = MutableStateFlow<List<String>>(emptyList())
    val autoCompAuthors: StateFlow<List<String>> = _autoCompAuthorsNames.asStateFlow()

    fun updateAutoComp(searchVal: String) {
        if (searchVal.isEmpty()) {
            _autoCompAuthorsNames.value = emptyList()
        } else {
            viewModelScope.launch {
                ledgeDb.authorDao().getAuthorNamesFuzzyFind(searchVal).collect { authors ->
                    _autoCompAuthorsNames.value = authors
                }
            }
        }
    }
}

class EditBookAuthorDialogViewModelFactory(private val ledgeDb: LedgeDatabase)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditBookAuthorDialogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditBookAuthorDialogViewModel(ledgeDb = ledgeDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
