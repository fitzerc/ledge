package com.github.fitzerc.ledge.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.fitzerc.ledge.data.models.BookAndRelations

@Composable
fun BookCard(book: BookAndRelations, onClick: (BookAndRelations) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(book) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = book.book.title, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Author: ${book.author.fullName}", style = MaterialTheme.typography.bodySmall)
        }
    }
}