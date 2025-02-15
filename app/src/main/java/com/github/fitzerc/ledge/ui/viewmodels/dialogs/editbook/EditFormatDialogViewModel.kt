package com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.BookFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditFormatDialogViewModel(private val ledgeDb: LedgeDatabase): ViewModel() {
    private val _formats = MutableStateFlow<List<BookFormat>>(emptyList())
    val formats: StateFlow<List<BookFormat>> = _formats.asStateFlow()

    init {
        viewModelScope.launch {
            ledgeDb.bookFormatDao().getBookFormatsAlpha().collect { formats ->
                _formats.value = formats
            }
        }
    }
}
class EditFormatDialogViewModelFactory(
    private val ledgeDb: LedgeDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditFormatDialogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditFormatDialogViewModel(ledgeDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
