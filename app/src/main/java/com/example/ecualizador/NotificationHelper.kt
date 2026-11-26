package com.example.ecualizador

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

/** Notificación fija con un botón para activar o desactivar el ecualizador sin abrir la app. */
object NotificationHelper {
    private const val CHANNEL = "eq"
    private const val ID = 1

    fun show(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        if (!nm.areNotificationsEnabled()) return
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, "Ecualizador", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val open = PendingIntent.getActivity(
            ctx, 0, Intent(ctx, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        val toggle = PendingIntent.getBroadcast(
            ctx, 1, Intent(ctx, ToggleReceiver::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        val on = EqEngine.enabled
        val n = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            .setContentTitle("Ecualizador")
            .setContentText(if (on) "Activado" else "Desactivado")
            .setContentIntent(open)
            .addAction(0, if (on) "Desactivar" else "Activar", toggle)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
        nm.notify(ID, n)
    }
}
