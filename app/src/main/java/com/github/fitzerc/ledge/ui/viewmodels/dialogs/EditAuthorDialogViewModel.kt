package com.github.fitzerc.ledge.ui.viewmodels.dialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.entities.Genre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditAuthorDialogViewModel(private val ledgeDb: LedgeDatabase): ViewModel() {
    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    init {
        viewModelScope.launch {
            ledgeDb.genreDao().getGenresAlpha().collect { genresList ->
                _genres.value = genresList
            }
        }
    }
}

class EditAuthorViewModelFactory(private val ledgeDb: LedgeDatabase)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditAuthorDialogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditAuthorDialogViewModel(ledgeDb = ledgeDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
