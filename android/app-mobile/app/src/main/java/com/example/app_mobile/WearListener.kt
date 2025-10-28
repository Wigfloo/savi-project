package com.example.app_mobile

import android.content.Context
import android.widget.Toast
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.savi.shared.api.*       // ApiClient, Measurement, IngestPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WearListener(
    private val context: Context,
    private val apiBaseUrl: String
) : MessageClient.OnMessageReceivedListener {

    private val api by lazy { ApiClient.create(apiBaseUrl) }

    override fun onMessageReceived(event: MessageEvent) {
        if (event.path == "/savi/ping") {
            val txt = event.data?.toString(Charsets.UTF_8) ?: "ping"
            Toast.makeText(context, "Ping del reloj: $txt", Toast.LENGTH_SHORT).show()

            // Ejemplo: enviar mini payload al backend
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val payload = IngestPayload(
                        user_id = "demo",
                        items = listOf(
                            Measurement(System.currentTimeMillis(), steps = 1, hr = null)
                        )
                    )
                    api.ingest(payload)
                } catch (_: Exception) { /* Ignorar errores de red */ }
            }
        }
    }

    fun register() = Wearable.getMessageClient(context).addListener(this)
    fun unregister() = Wearable.getMessageClient(context).removeListener(this)
}
