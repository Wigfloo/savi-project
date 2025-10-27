package com.example.app_mobile

import  android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
// Imports de Compose y Layouts
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.app_mobile.ui.theme.AppmobileTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.* // Repetido, pero mejor dejarlo si lo necesitas

// Imports de Corrutinas
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Imports de la API Compartida
import com.savi.shared.api.* // ApiClient, SaviApi, modelos
import com.savi.shared.api.ApiClient // También incluido en el * anterior

class MainActivity : ComponentActivity() {
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
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Listo") }
    // Asumiendo que ApiClient es parte de com.savi.shared.api.*, ya está importado.
    val api = remember { ApiClient.create("http://10.0.2.2:8000") } // Emulador → host

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "SAVI • Mobile")
        Spacer(Modifier.height(12.dp))

        Button(onClick = {
            scope.launch {
                status = "Ping…"
                // Asumo que tu interfaz ApiClient tiene un método health()
                val r = withContext(Dispatchers.IO) { api.health() }
                status = "Health: $r"
            }
        }) { Text("Probar /health") }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            scope.launch {
                status = "Ingest…"
                // Asumo que IngestPayload y Measurement son clases de tu módulo compartido (shared)
                val payload = IngestPayload(
                    user_id = "demo",
                    items = listOf(Measurement(System.currentTimeMillis(), 120, 85))
                )
                val r = withContext(Dispatchers.IO) { api.ingest(payload) }
                status = "Ingest OK: ${r.received}"
            }
        }) { Text("Enviar /v1/ingest") }

        Spacer(Modifier.height(16.dp))
        Text(status)
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppmobileTheme {
        Greeting("Android")
    }
}