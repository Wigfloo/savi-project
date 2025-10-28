package com.savi.wear // usa tu paquete

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Añadida importación de Color si es necesaria
import androidx.compose.ui.unit.dp

// Importaciones CORRECTAS para Wear OS
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text

// Wearable API y Corrutinas
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. Usamos el tema de Wear Compose
            MaterialTheme {
                // 2. Llamamos a la función Composable externa
                WearHome()
            }
        }
    }
}

// -------------------------------------------------------------

/**
 * Función Composable que define la UI del reloj.
 * Se define FUERA de la clase ComponentActivity.
 */
@Composable
fun WearHome() {
    // Referencia a la Activity actual para usar Wearable API
    val context = androidx.compose.ui.platform.LocalContext.current

    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Listo") }

    // Usamos el Scaffold de androidx.wear.compose.material
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        // En Wear Compose, el padding se maneja mejor en el Column.
        Column(Modifier.padding(12.dp)) {
            Text("SAVI • Wear")
            Spacer(Modifier.height(8.dp))

            // BOTÓN DE PING
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    try {
                        val nodeClient = Wearable.getNodeClient(context) // Usamos el contexto
                        val msgClient = Wearable.getMessageClient(context)

                        // 1. Obtener nodos conectados
                        val nodes = nodeClient.connectedNodes.await()
                        var sent = 0

                        // 2. Enviar mensaje a cada nodo (teléfono)
                        for (n in nodes) {
                            val res = msgClient
                                .sendMessage(n.id, "/savi/ping", "hola".toByteArray())
                                .await()
                            if (res >= 0) sent++
                        }
                        status = "Ping OK. Enviados: $sent"
                    } catch (e: Exception) {
                        status = "Error: ${e.message}"
                    }
                }
            }) {
                Text("Enviar ping")
            }

            Spacer(Modifier.height(8.dp))

            // CAMPO DE ESTADO
            Text(status)
        }
    }
}