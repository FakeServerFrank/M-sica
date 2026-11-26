package com.example.ecualizador

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EqEngine.init(applicationContext)
        val askNotif = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            NotificationHelper.show(applicationContext)
        }
        if (Build.VERSION.SDK_INT >= 33) askNotif.launch(Manifest.permission.POST_NOTIFICATIONS)
        else NotificationHelper.show(applicationContext)
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
                Surface(Modifier.fillMaxSize()) { EqScreen() }
            }
        }
    }
}

private fun label(hz: Int) = if (hz >= 1000) "%.1f kHz".format(hz / 1000f) else "$hz Hz"

@Composable
fun EqScreen() {
    val info = EqEngine.info
    val ctx = LocalContext.current
    Column(
        Modifier.fillMaxSize().systemBarsPadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Ecualizador", style = MaterialTheme.typography.headlineMedium)
        if (info == null) {
            Text("Este teléfono no permite usar el ecualizador de audio.")
            return@Column
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Activado", Modifier.weight(1f))
            Switch(EqEngine.enabled, { EqEngine.setEnabled(it); NotificationHelper.show(ctx) })
        }
        Text(
            "Reproductores conectados: ${EqEngine.connected}. Abre tu música: el ajuste se aplica cuando el reproductor avisa que empieza a sonar.",
            style = MaterialTheme.typography.bodySmall
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val n = info.bands
            fun preset(f: (Float) -> Float) = List(n) { i ->
                val t = if (n > 1) i / (n - 1f) else 0f
                (f(t) * info.max * 0.7f).roundToInt().coerceIn(info.min, info.max)
            }
            OutlinedButton({ EqEngine.setAll(preset { 0f }) }) { Text("Plano") }
            OutlinedButton({ EqEngine.setAll(preset { 1f - it }) }) { Text("Graves") }
            OutlinedButton({ EqEngine.setAll(preset { 1f - abs(it - 0.55f) * 3f }) }) { Text("Voces") }
            OutlinedButton({ EqEngine.setAll(preset { it }) }) { Text("Agudos") }
        }
        info.freqsHz.forEachIndexed { i, hz ->
            val db = EqEngine.levels.getOrElse(i) { 0 } / 100f
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label(hz), Modifier.width(76.dp), style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = db,
                    onValueChange = { EqEngine.setLevel(i, (it * 100).roundToInt()) },
                    valueRange = info.min / 100f..info.max / 100f,
                    modifier = Modifier.weight(1f)
                )
                Text("%+.1f".format(db), Modifier.width(48.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
