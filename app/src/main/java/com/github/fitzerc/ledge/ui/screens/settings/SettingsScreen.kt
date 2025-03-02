package com.github.fitzerc.ledge.ui.screens.settings

import android.content.Context
import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.ui.toastError
import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class SettingOption(
    val text: String,
    val onClick: () -> Unit
)

@Composable
fun SettingsScreen(navigationController: NavController, ledgeDb: LedgeDatabase) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val settingsOptions = listOf(
        SettingOption("Manage Authors", onClick = { navigationController.navigate("manageauthors") }),
        SettingOption("Manage Series", onClick = { navigationController.navigate("manageseries") }),
        SettingOption("Backup Data", onClick = { backupData(ledgeDb.dbVersion, context, coroutineScope) })
    )

    //TODO: may need bottomPadding passed in
    Scaffold(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) { paddingInner ->
        Column(modifier = Modifier.padding(paddingInner)) {
            Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                items(settingsOptions) { opt ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { opt.onClick() }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                style = MaterialTheme.typography.titleLarge,
                                text = opt.text,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
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
        toastError(
            "file written to: $outputFile",
            context,
            coroutineScope
        )
    } catch (e: IOException) {
        e.printStackTrace()
        toastError(
            "backup failed",
            context,
            coroutineScope
        )
    }
}