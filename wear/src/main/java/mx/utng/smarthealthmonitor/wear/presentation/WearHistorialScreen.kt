package mx.utng.smarthealthmonitor.wear.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.compose.layout.scrollAway

data class LecturaFC(val id: Int, val valorBpm: Int, val esNormal: Boolean, val hora: String)

@Composable
fun WearHistorialScreen(
    onBack: () -> Unit,
    viewModel: WearDashboardViewModel = viewModel()
) {
    val historial by viewModel.historial.collectAsState()
    val listState = rememberScalingLazyListState()
    val focusRequester = remember { FocusRequester() }
 
    // Pedir foco para recibir eventos de la corona
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
 
    Scaffold(
        timeText = {
            TimeText(modifier = Modifier.scrollAway { listState })
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            state    = listState,
            modifier = Modifier
                .fillMaxSize()
                .rotaryScrollable(  // ← conecta la corona
                    behavior = RotaryScrollableDefaults.behavior(
                        scrollableState = listState
                    ),
                    focusRequester = focusRequester
                )
                .focusRequester(focusRequester) // ← Asegurar que el focusRequester se aplica al modificador
        ) {
            item {
                Text("Historial (${historial.size})",
                     style = MaterialTheme.typography.title3,
                     modifier = Modifier.padding(8.dp))
            }
            if (historial.isEmpty()) {
                item {
                    Text("Sin lecturas aún",
                         style = MaterialTheme.typography.body2,
                         modifier = Modifier.padding(8.dp))
                }
            } else {
                items(historial, key = { it.id }) { lectura ->
                    WearFilaHistorial(lectura = lectura)
                }
            }
        }
    }
}

@Composable
fun WearFilaHistorial(lectura: LecturaFC) {
    val color = if (lectura.esNormal)
        MaterialTheme.colors.primary
    else
        MaterialTheme.colors.error
 
    Chip(
        label = { Text("${lectura.valorBpm} bpm",
                       color = color) },
        secondaryLabel = { Text(lectura.hora) },
        onClick = { },
        colors = ChipDefaults.secondaryChipColors(),
        modifier = Modifier.fillMaxWidth()
    )
}
