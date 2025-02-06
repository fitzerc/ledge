package com.github.fitzerc.ledge.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.BookFormat
import com.github.fitzerc.ledge.data.entities.Genre
import com.github.fitzerc.ledge.data.entities.ReadStatus
import com.github.fitzerc.ledge.data.models.BookAndRelations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchFilterDialogViewModel(private val ledgeDb: LedgeDatabase): ViewModel() {
    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    private val _readStatuses = MutableStateFlow<List<ReadStatus>>(emptyList())
    val readStatuses: StateFlow<List<ReadStatus>> = _readStatuses

    private val _bookFormats = MutableStateFlow<List<BookFormat>>(emptyList())
    val bookFormats: StateFlow<List<BookFormat>> = _bookFormats

    init {
        viewModelScope.launch {
            ledgeDb.genreDao().getGenresAlpha().collect() { genresList ->
                _genres.value = genresList
            }
        }
        viewModelScope.launch {
            ledgeDb.readStatusDao().getReadStatuses().collect() { readStatusList ->
                _readStatuses.value = readStatusList
            }
        }
        viewModelScope.launch {
            ledgeDb.bookFormatDao().getBookFormatsAlpha().collect() { bookFormatList ->
                _bookFormats.value = bookFormatList
            }
        }
    }
}
class SearchFilterDialogViewModelFactory(private val ledgeDb: LedgeDatabase)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchFilterDialogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchFilterDialogViewModel(ledgeDb = ledgeDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
