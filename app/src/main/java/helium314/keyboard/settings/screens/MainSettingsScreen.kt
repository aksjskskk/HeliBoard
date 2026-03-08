// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.settings.screens

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import helium314.keyboard.latin.R
// ✅ استدعاء الملفات الجديدة
import helium314.keyboard.latin.UserBannedWordsActivity 
import helium314.keyboard.latin.utils.JniUtils
import helium314.keyboard.latin.utils.SubtypeLocaleUtils.displayName
import helium314.keyboard.latin.utils.SubtypeSettings
import helium314.keyboard.latin.utils.prefs
import helium314.keyboard.settings.NextScreenIcon
import helium314.keyboard.settings.SearchSettingsScreen
import helium314.keyboard.settings.Theme
import helium314.keyboard.settings.dialogs.ListPickerDialog
import helium314.keyboard.settings.dialogs.TextInputDialog
import helium314.keyboard.settings.initPreview
import helium314.keyboard.settings.preferences.Preference
import helium314.keyboard.settings.previewDark
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.edit

@Composable
fun MainSettingsScreen(
    onClickAbout: () -> Unit,
    onClickTextCorrection: () -> Unit,
    onClickPreferences: () -> Unit,
    onClickToolbar: () -> Unit,
    onClickGestureTyping: () -> Unit,
    onClickAdvanced: () -> Unit,
    onClickAppearance: () -> Unit,
    onClickLanguage: () -> Unit,
    onClickLayouts: () -> Unit,
    onClickDictionaries: () -> Unit,
    onClickBack: () -> Unit,
) {
    SearchSettingsScreen(
        onClickBack = onClickBack,
        title = stringResource(R.string.ime_settings),
        settings = emptyList(),
    ) {
        val enabledSubtypes = SubtypeSettings.getEnabledSubtypes(true)
        val context = LocalContext.current // لفتح الشاشات

        Scaffold(contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)) { innerPadding ->
            Column(
                Modifier.verticalScroll(rememberScrollState()).then(Modifier.padding(innerPadding))
            ) {
                Preference(
                    name = stringResource(R.string.language_and_layouts_title),
                    description = enabledSubtypes.joinToString(", ") { it.displayName() },
                    onClick = onClickLanguage,
                    icon = R.drawable.ic_settings_languages
                ) { NextScreenIcon() }

                Preference(
                    name = stringResource(R.string.settings_screen_preferences),
                    onClick = onClickPreferences,
                    icon = R.drawable.ic_settings_preferences
                ) { NextScreenIcon() }

                // ========================================================
                // 🔒 1. Block Words Manager (زر الكلمات)
                // ========================================================
                Preference(
                    name = "Block Words Manager",
                    description = "Add custom words to blocklist (Permanent)",
                    onClick = {
                        val intent = Intent(context, UserBannedWordsActivity::class.java)
                        context.startActivity(intent)
                    },
                    icon = android.R.drawable.ic_lock_lock
                ) { NextScreenIcon() }

                // ========================================================
                // ⏱️ 2. Time Blocking (Ban Duration)
                // ========================================================
                var showBanDurationDialog by rememberSaveable { mutableStateOf(false) }
                val prefs = context.prefs()
                val currentBanDuration = prefs.getLong("punishment_duration_millis", 300000L) // Default 5 mins

                val banOptions = listOf(
                    "2 minutes" to 120000L,
                    "3 minutes" to 180000L,
                    "5 minutes" to 300000L
                )
                val selectedBanOption = banOptions.firstOrNull { it.second == currentBanDuration } ?: banOptions[2]

                Preference(
                    name = "Time Blocking",
                    description = selectedBanOption.first,
                    onClick = { showBanDurationDialog = true },
                    icon = android.R.drawable.ic_menu_recent_history
                )

                if (showBanDurationDialog) {
                    ListPickerDialog(
                        onDismissRequest = { showBanDurationDialog = false },
                        items = banOptions,
                        onItemSelected = {
                            prefs.edit { putLong("punishment_duration_millis", it.second) }
                            showBanDurationDialog = false
                        },
                        selectedItem = selectedBanOption,
                        title = { Text("Select Ban Duration") },
                        getItemName = { it.first }
                    )
                }

                // ========================================================
                // 💬 3. Toast Message (Custom Ban Message)
                // ========================================================
                var showToastMessageDialog by rememberSaveable { mutableStateOf(false) }
                val defaultToastMsg = "The keyboard is blocked."
                val currentToastMsg = prefs.getString("custom_blocked_toast_message", defaultToastMsg) ?: defaultToastMsg

                Preference(
                    name = "Toast Message",
                    description = currentToastMsg,
                    onClick = { showToastMessageDialog = true },
                    icon = android.R.drawable.ic_dialog_info
                )

                if (showToastMessageDialog) {
                    TextInputDialog(
                        onDismissRequest = { showToastMessageDialog = false },
                        onConfirmed = { text ->
                            prefs.edit { putString("custom_blocked_toast_message", text.ifBlank { defaultToastMsg }) }
                            showToastMessageDialog = false
                        },
                        initialText = currentToastMsg,
                        title = { Text("Toast Message") }
                    )
                }
                // ========================================================

                Preference(
                    name = stringResource(R.string.settings_screen_appearance),
                    onClick = onClickAppearance,
                    icon = R.drawable.ic_settings_appearance
                ) { NextScreenIcon() }

                Preference(
                    name = stringResource(R.string.settings_screen_toolbar),
                    onClick = onClickToolbar,
                    icon = R.drawable.ic_settings_toolbar
                ) { NextScreenIcon() }

                if (JniUtils.sHaveGestureLib)
                    Preference(
                        name = stringResource(R.string.settings_screen_gesture),
                        onClick = onClickGestureTyping,
                        icon = R.drawable.ic_settings_gesture
                    ) { NextScreenIcon() }

                Preference(
                    name = stringResource(R.string.settings_screen_correction),
                    onClick = onClickTextCorrection,
                    icon = R.drawable.ic_settings_correction
                ) { NextScreenIcon() }

                Preference(
                    name = stringResource(R.string.settings_screen_secondary_layouts),
                    onClick = onClickLayouts,
                    icon = R.drawable.ic_ime_switcher
                ) { NextScreenIcon() }

                Preference(
                    name = stringResource(R.string.dictionary_settings_category),
                    onClick = onClickDictionaries,
                    icon = R.drawable.ic_dictionary
                ) { NextScreenIcon() }

                Preference(
                    name = stringResource(R.string.settings_screen_advanced),
                    onClick = onClickAdvanced,
                    icon = R.drawable.ic_settings_advanced
                ) { NextScreenIcon() }

                Preference(
                    name = stringResource(R.string.settings_screen_about),
                    onClick = onClickAbout,
                    icon = R.drawable.ic_settings_about
                ) { NextScreenIcon() }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewScreen() {
    initPreview(LocalContext.current)
    Theme(previewDark) {
        Surface {
            MainSettingsScreen({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})
        }
    }
}
