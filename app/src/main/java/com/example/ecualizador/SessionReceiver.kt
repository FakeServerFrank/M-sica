package com.example.ecualizador

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect

/** Recibe el aviso de los reproductores (Spotify, YouTube Music, etc.) y conecta el ecualizador a su audio. */
class SessionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        EqEngine.init(context.applicationContext)
        val session = intent.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, -1)
        if (session <= 0) return
        when (intent.action) {
            AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION -> {
                EqEngine.attach(session)
                NotificationHelper.show(context.applicationContext)
            }
            AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION -> EqEngine.detach(session)
        }
    }
}
