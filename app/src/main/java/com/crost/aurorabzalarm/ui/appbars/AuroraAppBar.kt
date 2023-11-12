package com.crost.aurorabzalarm.ui.appbars

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.crost.aurorabzalarm.R


@Preview//(uiMode = Configuration.UI_MODE_NIGHT_MASK)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuroraAppBar(){
    TopAppBar(
//        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
//            titleContentColor = MaterialTheme.colorScheme.primary,
//        ),
        title = {
            Text(stringResource(R.string.app_name))
        },
        actions = {
            AppBarActions()
        }
    )
}
