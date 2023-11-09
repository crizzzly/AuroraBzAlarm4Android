package com.crost.aurorabzalarm.settings.uielements.sections

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.settings.Settings
import com.crost.aurorabzalarm.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationStateSection(
    con: Context,
    notificationEnabled: Boolean,
    settingsConfig: Settings,
    columnScope: ColumnScope
){
    val viewModel: SettingsViewModel = viewModel()

//    val notification = remember { mutableStateOf(notificationEnabled) }

    ListItem( // Enable/Disable
        headlineContent = {
            Text(
                "Enable Aurora Alarm",
                textAlign = TextAlign.Right
            )
        },
        trailingContent = {
            Switch(
                checked = notificationEnabled,
                onCheckedChange =
                { checked ->
                    Log.d("Settings Screen", "alarm switch enabled : $checked")
//                    notificationEnabled = checked
                    viewModel.setNotificationState(checked)
                    settingsConfig.notificationEnabled = checked
                    viewModel.saveConfig(con, settingsConfig)
                }
            )
        },
//        modifier = columnScope.Modifier.align(columnScope.End)
    )



}