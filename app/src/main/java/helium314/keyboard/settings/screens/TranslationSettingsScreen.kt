package helium314.keyboard.settings.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateRemoteModel
import helium314.keyboard.latin.R
import helium314.keyboard.settings.SearchScreen
import java.util.Locale

@Composable
fun TranslationSettingsScreen(onClickBack: () -> Unit) {
    val modelManager = RemoteModelManager.getInstance()

    var downloadedModels by remember { mutableStateOf<Set<String>>(emptySet()) }
    var downloadingModels by remember { mutableStateOf<Set<String>>(emptySet()) }

    val refreshModels = {
        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                downloadedModels = models.map { it.language }.toSet()
            }
            .addOnFailureListener {
                // Ignore
            }
    }

    LaunchedEffect(Unit) {
        refreshModels()
    }

    val allLanguages = TranslateLanguage.getAllLanguages()

    SearchScreen<String>(
        onClickBack = onClickBack,
        title = { Text("Translation Models") },
        filteredItems = { term ->
            allLanguages.filter { Locale(it).displayLanguage.contains(term, ignoreCase = true) }
        },
        itemContent = { langCode ->
            val isDownloaded = downloadedModels.contains(langCode)
            val isDownloading = downloadingModels.contains(langCode)
            val displayName = Locale(langCode).displayLanguage

            ListItem(
                headlineContent = { Text(displayName) },
                supportingContent = { Text(langCode) },
                trailingContent = {
                    if (isDownloading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else if (isDownloaded) {
                        IconButton(onClick = {
                            val model = TranslateRemoteModel.Builder(langCode).build()
                            modelManager.deleteDownloadedModel(model).addOnSuccessListener {
                                refreshModels()
                            }
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close_gray),
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            downloadingModels = downloadingModels + langCode
                            val model = TranslateRemoteModel.Builder(langCode).build()
                            modelManager.download(model, com.google.mlkit.common.model.DownloadConditions.Builder().build())
                                .addOnSuccessListener {
                                    downloadingModels = downloadingModels - langCode
                                    refreshModels()
                                }
                                .addOnFailureListener {
                                    downloadingModels = downloadingModels - langCode
                                    refreshModels()
                                }
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add_white),
                                contentDescription = "Download"
                            )
                        }
                    }
                }
            )
            HorizontalDivider()
        },
        content = null
    )
}
