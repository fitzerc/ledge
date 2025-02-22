package com.github.fitzerc.ledge.ui.screens

import android.content.Context
import android.os.Environment
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.ui.ToastError
import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun SettingsScreen(ledgeDb: LedgeDatabase) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.padding(horizontal = 16.dp)) { paddingInner ->
        Button(
            modifier = Modifier.padding(paddingInner),
            onClick = { backupData(ledgeDb.dbVersion, context, coroutineScope) }
        ) {
            Text(text = "Backup Data")
        }
    }
}

fun backupData(dbVersion: String, context: Context, coroutineScope: CoroutineScope) {
    val dbName = "ledge_db$dbVersion"
    val dbPath = context.getDatabasePath(dbName).absolutePath
    val documentsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    val backupPath = File(documentsPath, dbName).absolutePath

    try {
        val inputFile = File(dbPath)
        val outputFile = File(backupPath)

        FileInputStream(inputFile).use { input ->
            FileOutputStream(outputFile).use { output ->
                val buffer = ByteArray(1024)
                var length: Int
                while (input.read(buffer).also { length = it } > 0) {
                    output.write(buffer, 0, length)
                }
            }
        }
        ToastError(
            "file written to: $outputFile",
            context,
            coroutineScope
        )
    } catch (e: IOException) {
        e.printStackTrace()
        ToastError(
            "backup failed",
            context,
            coroutineScope
        )
    }
}