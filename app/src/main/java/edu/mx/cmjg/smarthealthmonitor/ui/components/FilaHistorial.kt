package edu.mx.cmjg.smarthealthmonitor.ui.components
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.mx.cmjg.smarthealthmonitor.data.models.LecturaFC
import edu.mx.cmjg.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff

// ui/components/FilaHistorial.kt
@Composable
fun FilaHistorial(
    lectura: LecturaFC,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Valor FC con color según si es normal o no
        Text(
            text = "${lectura.bpm} bpm",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = if (lectura.estado == "Normal") 
                MaterialTheme.colorScheme.onSurface 
            else MaterialTheme.colorScheme.error
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = lectura.hora,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            androidx.compose.material3.Icon(
                imageVector = if (lectura.sincronizado) 
                    androidx.compose.material.icons.Icons.Default.CloudDone 
                else 
                    androidx.compose.material.icons.Icons.Default.CloudOff,
                contentDescription = if (lectura.sincronizado) "Sincronizado" else "Pendiente de sync",
                tint = if (lectura.sincronizado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

