package edu.mx.cmjg.smarthealthmonitor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import edu.mx.cmjg.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme

@Composable
fun AlertaScreen(
    fc: Int,                       // FC actual del Dashboard
    onDismiss: () -> Unit,          // Cancelar / cerrar
    onConfirmar: () -> Unit         // Confirmar y enviar alerta
) {
    var enviando by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector       = Icons.Default.Warning,
                contentDescription = null,
                tint              = MaterialTheme.colorScheme.error,
                modifier          = Modifier.size(36.dp)
            )
        },
        title = {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    text  = "Enviar alerta de emergencia",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                androidx.compose.ui.viewinterop.AndroidView(
                    factory = { ctx ->
                        val themedCtx = androidx.appcompat.view.ContextThemeWrapper(ctx, androidx.appcompat.R.style.Theme_AppCompat_NoActionBar)
                        androidx.mediarouter.app.MediaRouteButton(themedCtx).apply {
                            try {
                                com.google.android.gms.cast.framework.CastButtonFactory.setUpMediaRouteButton(themedCtx, this)
                            } catch (e: Exception) {
                                android.util.Log.e("CastButton", "Error setting up MediaRouteButton", e)
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text  = "FC actual: $fc bpm",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Se notificará a tus contactos de emergencia.\n" +
                           "Esta acción no se puede deshacer."
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    enviando = true
                    onConfirmar()
                },
                enabled = !enviando,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor   = MaterialTheme.colorScheme.onError
                )
            ) {
                if (enviando) {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(20.dp),
                        color     = MaterialTheme.colorScheme.onError,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("CONFIRMAR ALERTA",
                         style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true, name = "Alerta - Light", widthDp = 400, heightDp = 800)
@Preview(showBackground = true, name = "Alerta - Dark", widthDp = 400, heightDp = 800,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AlertaScreenPreview() {
    SmartHealthMonitorTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AlertaScreen(fc = 145, onDismiss = { }, onConfirmar = { })
        }
    }
}
