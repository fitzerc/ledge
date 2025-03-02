package com.github.fitzerc.ledge.ui.viewmodels.dialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.BookFormat
import com.github.fitzerc.ledge.data.entities.Genre
import com.github.fitzerc.ledge.data.entities.ReadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddBookDialogViewModel(private val ledgeDb: LedgeDatabase): ViewModel() {
        private val _genres = MutableStateFlow<List<Genre>>(emptyList())
        val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

        private val _readStatuses = MutableStateFlow<List<ReadStatus>>(emptyList())
        val readStatuses: StateFlow<List<ReadStatus>> = _readStatuses

        private val _bookFormats = MutableStateFlow<List<BookFormat>>(emptyList())
        val bookFormats: StateFlow<List<BookFormat>> = _bookFormats

        private val _autoCompAuthorsNames = MutableStateFlow<List<String>>(emptyList())
        val autoCompAuthors: StateFlow<List<String>> = _autoCompAuthorsNames.asStateFlow()

        init {
            viewModelScope.launch {
                ledgeDb.genreDao().getGenresAlpha().collect { genericList ->
                    _genres.value = genericList
                }
            }
            viewModelScope.launch {
                ledgeDb.readStatusDao().getReadStatuses().collect { readStatusList ->
                    _readStatuses.value = readStatusList
                }
            }
            viewModelScope.launch {
                ledgeDb.bookFormatDao().getBookFormatsAlpha().collect { bookFormatList ->
                    _bookFormats.value = bookFormatList
                }
            }
        }

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

    class AddBookViewModelFactory(private val ledgeDb: LedgeDatabase)
        : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddBookDialogViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddBookDialogViewModel(ledgeDb = ledgeDb) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }