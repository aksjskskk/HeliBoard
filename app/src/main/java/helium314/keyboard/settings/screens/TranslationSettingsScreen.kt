package helium314.keyboard.settings.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import helium314.keyboard.latin.R
import helium314.keyboard.settings.SearchScreen
import java.util.Locale

@Composable
fun TranslationSettingsScreen(onClickBack: () -> Unit) {
    // Cloud-based translation - no model downloads needed
    val allLanguages = listOf("auto", "en", "es", "fr", "de", "ar", "zh", "ja", "ko", "ru", "it", "pt", "nl", "pl", "tr", "hi", "bn", "ta", "te", "mr", "ur", "gu", "kn", "ml", "pa", "or", "as", "ne", "si", "my", "km", "lo", "ka", "am", "sw", "zu", "xh", "af", "sq", "az", "be", "bs", "bg", "ca", "hr", "cs", "da", "et", "fi", "gl", "el", "he", "hu", "is", "id", "ga", "lv", "lt", "mk", "ms", "mt", "no", "fa", "ro", "sr", "sk", "sl", "sv", "th", "uk", "vi", "cy", "yi")

    SearchScreen<String>(
        onClickBack = onClickBack,
        title = { Text("Translation Languages") },
        filteredItems = { term ->
            allLanguages.filter { Locale(it).displayLanguage.contains(term, ignoreCase = true) || it.equals(term, ignoreCase = true) }
        },
        itemContent = { langCode ->
            val displayName = if (langCode == "auto") "Auto Detect" else Locale(langCode).displayLanguage

            ListItem(
                headlineContent = { Text(displayName) },
                supportingContent = { Text(langCode) }
            )
            HorizontalDivider()
        },
        content = null
    )
}
