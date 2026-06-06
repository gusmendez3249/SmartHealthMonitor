package edu.mx.cmjg.smarthealthmonitor.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Importación necesaria
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning // Importación necesaria
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState // Importación para StateFlow
import androidx.compose.runtime.getValue        // Importación para delegación 'by'
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // Importación para inyección automática
import edu.mx.cmjg.smarthealthmonitor.BuildConfig
import edu.mx.cmjg.smarthealthmonitor.data.models.LecturaFC
import edu.mx.cmjg.smarthealthmonitor.data.models.MockData
import edu.mx.cmjg.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme
import edu.mx.cmjg.smarthealthmonitor.ui.viewmodel.DashboardViewModel
// Asegúrate de importar el repositorio si se encuentra en otra ruta:
// import edu.mx.cmjg.smarthealthmonitor.data.repository.SmartHealthRepository

@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar
@Composable
fun DashboardScreen(
    onHistorialClick: () -> Unit = {},
    onAlertClick: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel() // ← Inyección automática del ViewModel
) {
    // Convierte los StateFlow del ViewModel en Estados reactivos de Compose
    val fc by viewModel.fc.collectAsState()
    val pasos by viewModel.pasos.collectAsState()
    val historial by viewModel.historial.collectAsState()

    SmartHealthMonitorTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "SmartHealth",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor    = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick       = onAlertClick,
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        imageVector       = Icons.Default.Warning,
                        contentDescription = "Enviar alerta de emergencia",
                        tint              = MaterialTheme.colorScheme.onError
                    )
                }
            }
        ) { paddingValues ->
            // ⚠️ paddingValues OBLIGATORIO
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding        = PaddingValues(16.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp)
            ) {
                // ── Tarjeta FC ────────────────────────────
                item {
                    TarjetaDato(
                        valor      = "$fc",
                        unidad     = "bpm",
                        label      = "Frecuencia cardíaca",
                        colorValor = if (fc in 60..100) Color.Blue else Color.Red
                    )
                }
                // ── Tarjeta Pasos ─────────────────────────
                item {
                    TarjetaDato(
                        valor      = "%,d".format(pasos),
                        unidad     = "pasos",
                        label      = "Pasos del día",
                        colorValor = MaterialTheme.colorScheme.primary
                    )
                }
                // ── Encabezado historial ──────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text("Historial reciente",
                            style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = onHistorialClick) {
                            Text("Ver todo")
                        }
                    }
                }
                // ── Lista del historial ───────────────────
                items(historial, key = { it.id }) { lectura ->
                    FilaHistorial(lectura = lectura)
                }

                // ── Botón de simulación — SOLO PARA DEBUG ──
                item {
                    if (BuildConfig.DEBUG) {
                        OutlinedButton(
                            onClick = {
                                // 🚀 Ahora sí ejecuta la función del ViewModel
                                viewModel.simularDatosWearable()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Simular dato del wearable (DEBUG)")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Dashboard - Light",
    showSystemUi = true, device = "id:pixel_6")
@Preview(showBackground = true, name = "Dashboard - Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DashboardScreenPreview() {
    SmartHealthMonitorTheme {
        DashboardScreen()
    }
}