package com.github.fitzerc.ledge.ui

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun toastError(message: String, context: Context, coroutineScope: CoroutineScope) {
    coroutineScope.launch {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
