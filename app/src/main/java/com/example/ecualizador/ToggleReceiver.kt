package com.example.ecualizador

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Se ejecuta al pulsar el botón de la notificación. */
class ToggleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        EqEngine.init(context.applicationContext)
        EqEngine.setEnabled(!EqEngine.enabled)
        NotificationHelper.show(context.applicationContext)
    }
}
