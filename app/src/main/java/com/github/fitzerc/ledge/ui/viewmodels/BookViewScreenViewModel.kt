package com.github.fitzerc.ledge.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.Book
import com.github.fitzerc.ledge.data.models.AuthorAndGenre
import com.github.fitzerc.ledge.data.models.BookAndRelations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewScreenViewModel(val bookId: Int, private val ledgeDb: LedgeDatabase): ViewModel() {
    private val _book = MutableStateFlow<BookAndRelations?>(null)
    val book: StateFlow<BookAndRelations?> = _book.asStateFlow()

    init {
        viewModelScope.launch {
            ledgeDb.bookDao().getBookById(bookId).collect() { book ->
                _book.value = book
            }
        }
    }

    fun refreshBook(bookId: Int) {
        viewModelScope.launch {
            ledgeDb.bookDao().getBookById(bookId).collect() { book ->
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
                    //TODO: toast error
                    val err = "author with name: $newFullName not found, unable to update"
                    println(err)
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
}

class BookViewScreenViewModelFactory(
    private val ledgeDb: LedgeDatabase,
    private val bookId: Int
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewScreenViewModel(bookId, ledgeDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}