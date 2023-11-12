package com.crost.aurorabzalarm.ui.screens.settings.uielements.sections

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun SettingsHeadlineSection() {
    ListItem(  // Headline Warning Level
        headlineContent = { Text("Warning Levels") },
        colors = ListItemColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            headlineColor = MaterialTheme.colorScheme.primary,
            supportingTextColor = MaterialTheme.colorScheme.primary,
            leadingIconColor = MaterialTheme.colorScheme.primaryContainer,
            trailingIconColor = MaterialTheme.colorScheme.primaryContainer,
            disabledHeadlineColor = MaterialTheme.colorScheme.secondary,
            disabledLeadingIconColor = MaterialTheme.colorScheme.secondary,
            disabledTrailingIconColor = MaterialTheme.colorScheme.secondary,
            overlineColor = MaterialTheme.colorScheme.secondary,
        )
    )
}