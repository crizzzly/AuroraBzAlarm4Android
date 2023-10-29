package com.crost.aurorabzalarm.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.crost.aurorabzalarm.utils.Constants.PADDING_L
import com.crost.aurorabzalarm.viewmodels.ViewModelFactory

//@Preview
@Composable
fun AlarmSettingsDialog(){
    val viewModel = ViewModelFactory.getDataViewModel()
    val openSettingsDialog = remember { viewModel.alarmSettingsVisible }
    val switchEnabled by remember { viewModel.alarmIsEnabled }

    when {
        openSettingsDialog.value -> {
            // TODO: Find out why background is transparent
            Dialog(
                onDismissRequest = { openSettingsDialog.value = false },
            ) {
                Surface(
                    color = Color(0xFF040026),
                    shape = RoundedCornerShape(10.dp)
                ) {

                }
                Column(
                    modifier = Modifier
                        .fillMaxSize(1f)
                        .padding(PADDING_L.dp),

                ) {
                    Text(
                        "Alarm Settings ",
                        color = Color.LightGray,
                        modifier = Modifier
                            .padding(PADDING_L.dp, 0.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    HorizontalDivider(modifier = Modifier.padding(0.dp, PADDING_L.dp))

                    Box(modifier = Modifier
//                         .align(Alignment.End)
                        .fillMaxWidth(),
                        ){
                            Text(
                                "Enable Alarm",
                                color = Color.LightGray,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                            Switch(
                                checked = switchEnabled,
                                onCheckedChange = {
                                    viewModel.setAuroraAlarm(it)
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                            )

                    }
                }
            }
        }
    }
}