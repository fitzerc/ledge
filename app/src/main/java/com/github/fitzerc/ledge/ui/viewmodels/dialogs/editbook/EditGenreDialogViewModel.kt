package com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.Genre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditGenreDialogViewModel(private val ledgeDb: LedgeDatabase) : ViewModel() {
    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    init {
        viewModelScope.launch {
            ledgeDb.genreDao().getGenresAlpha().collect() { genres ->
                _genres.value = genres
            }
        }
    }
}

class EditGenreDialogViewModelFactory(
    private val ledgeDb: LedgeDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditGenreDialogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditGenreDialogViewModel(ledgeDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}