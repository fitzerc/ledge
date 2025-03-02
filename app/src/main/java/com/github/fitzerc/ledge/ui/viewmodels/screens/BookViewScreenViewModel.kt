package com.github.fitzerc.ledge.ui.viewmodels.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.Book
import com.github.fitzerc.ledge.data.entities.Series
import com.github.fitzerc.ledge.data.models.AuthorAndGenre
import com.github.fitzerc.ledge.data.models.BookAndRelations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewScreenViewModel(
    private val bookId: Int,
    private val ledgeDb: LedgeDatabase,
    private val toastError: (message: String)-> Unit
): ViewModel() {
    private val _book = MutableStateFlow<BookAndRelations?>(null)
    val book: StateFlow<BookAndRelations?> = _book.asStateFlow()

    init {
        viewModelScope.launch {
            ledgeDb.bookDao().getBookById(bookId).collect { book ->
                _book.value = book
            }
        }
    }

    fun refreshBook(bookId: Int) {
        viewModelScope.launch {
            ledgeDb.bookDao().getBookById(bookId).collect { book ->
                _book.value = book
            }
        }
    }

    private suspend fun getAuthor(fullName: String): AuthorAndGenre? {
        return ledgeDb.authorDao().getAuthorByName(fullName)
    }

    fun updateBookAuthor(book: Book, newFullName: String) {
        viewModelScope.launch {
            val author = getAuthor(newFullName)
            author?.let { updateBook(book.copy(authorId = it.author.authorId)) }
                ?: run {
                    val err = "author with name: $newFullName not found, unable to update"
                    println(err)
                    toastError(err)
                }
        }
    }

    fun updateBook(book: Book) {
        viewModelScope.launch {
            ledgeDb.bookDao().updateBook(book)
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            ledgeDb.bookDao().deleteBook(book)
        }
    }

    fun updateBookWithSeries(book: Book, seriesName: String) {
        viewModelScope.launch {
            var series = ledgeDb.seriesDao().getSeriesByName(seriesName)
            if (series == null) {
                ledgeDb.seriesDao().updateSeries(Series(seriesName = seriesName))
                series = ledgeDb.seriesDao().getSeriesByName(seriesName)
            }

            if (series == null) {
                throw Exception("something went wrong - unable to add/find series")
            }

            ledgeDb.bookDao().updateBook(book.copy(partOfSeriesId = series.seriesId))
        }
    }
}

class BookViewScreenViewModelFactory(
    private val ledgeDb: LedgeDatabase,
    private val bookId: Int,
    private val toastError: (message: String) -> Unit
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewScreenViewModel(bookId, ledgeDb, toastError) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}