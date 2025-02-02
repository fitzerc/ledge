package com.github.fitzerc.ledge.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.models.BookAndRelations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BooksScreenViewModel(private val ledgeDb: LedgeDatabase): ViewModel() {
    private val _searchResults = MutableStateFlow<List<BookAndRelations>>(emptyList())
    val searchResults: StateFlow<List<BookAndRelations>> = _searchResults.asStateFlow()

    fun getSearchBooks(searchParam: String) {
        viewModelScope.launch {
            val query = if (searchParam == "*") "" else searchParam
            ledgeDb.bookDao().getBooksByTitleOrAuthor(query).collect() { books ->
                _searchResults.value = books
            }
        }
    }
}

class BooksScreenViewModelFactory(private val ledgeDb: LedgeDatabase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BooksScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BooksScreenViewModel(ledgeDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}