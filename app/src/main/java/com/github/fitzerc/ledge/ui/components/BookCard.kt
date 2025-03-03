package com.github.fitzerc.ledge.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Text(text = book.book.title, style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "By: ${book.author.fullName}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                if (book.partOfSeries != null) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(end = 100.dp)
                    ) {
                        Text(text = "Series: ${book.partOfSeries.seriesName}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}