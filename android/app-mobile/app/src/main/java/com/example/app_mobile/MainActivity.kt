package com.example.app_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_mobile.ui.theme.AppmobileTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.savi.shared.api.ApiClient
import com.savi.shared.api.IngestPayload
import com.savi.shared.api.Measurement


/**
 * Nota: Asumo que la clase WearListener y la interfaz WearListener tienen estas dependencias.
 * Si WearListener no está definida, tendrás que crearla o importarla.
 */
// private lateinit var wearListener: WearListener
// ^^^ Esta declaración DEBE ir dentro de la clase si es un miembro de la Activity.


class MainActivity : ComponentActivity() {

    // 👇 Esta línea es la que faltaba
    private lateinit var wearListener: WearListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppmobileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // 👇 crea e inicia el listener
        wearListener = WearListener(this, "http://192.168.1.3:8000")
        wearListener.register()
    }

    override fun onStop() {
        // 👇 asegúrate de que se apague correctamente
        wearListener.unregister()
        super.onStop()
    }
}


// -------------------------------------------------------------

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    // Uso de rememberCoroutineScope para lanzar corrutinas
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Listo") }

    // Asumiendo que ApiClient.create existe y que la IP es correcta
    val api = remember { ApiClient.create("http://192.168.1.3:8000") }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "SAVI • Mobile")
        Spacer(Modifier.height(12.dp))

        // BOTÓN 1: Probar Health
        Button(onClick = {
            scope.launch {
                status = "Ping…"
                try {
                    val r = withContext(Dispatchers.IO) { api.health() }
                    status = "Health OK: $r"
                } catch (e: Exception) {
                    status = "Health Error: ${e.message}"
                }
            }
        }) { Text("Probar /health") }

        Spacer(Modifier.height(8.dp))

        // BOTÓN 2: Enviar Ingest
        Button(onClick = {
            scope.launch {
                status = "Ingest…"
                try {
                    val payload = IngestPayload(
                        user_id = "demo",
                        items = listOf(Measurement(System.currentTimeMillis(), 120, 85))
                    )
                    val r = withContext(Dispatchers.IO) { api.ingest(payload) }
                    status = "Ingest OK: ${r.received}"
                } catch (e: Exception) {
                    status = "Ingest Error: ${e.message}"
                }
            }
        }) { Text("Enviar /v1/ingest") }

        Spacer(Modifier.height(16.dp))
        Text(status)
    }
}


// -------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppmobileTheme {
        Greeting("Android")
    }
}