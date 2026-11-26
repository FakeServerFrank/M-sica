package com.example.ecualizador

import android.content.Context
import android.content.SharedPreferences
import android.media.audiofx.Equalizer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class EqInfo(val bands: Int, val min: Int, val max: Int, val freqsHz: List<Int>)

/** Guarda los ajustes y los aplica a cada sesión de audio conectada. Niveles en milibelios (100 = 1 dB). */
object EqEngine {
    private var prefs: SharedPreferences? = null
    private val effects = HashMap<Int, Equalizer>()

    var info: EqInfo? = null
        private set
    val levels = mutableStateListOf<Int>()
    var enabled by mutableStateOf(true)
    var connected by mutableIntStateOf(0)

    @Synchronized
    fun init(ctx: Context) {
        if (prefs != null) return
        val p = ctx.getSharedPreferences("eq", Context.MODE_PRIVATE)
        prefs = p
        try {
            val e = Equalizer(0, 0)
            val r = e.bandLevelRange
            val n = e.numberOfBands.toInt()
            info = EqInfo(n, r[0].toInt(), r[1].toInt(),
                (0 until n).map { e.getCenterFreq(it.toShort()) / 1000 })
            e.release()
        } catch (t: Throwable) { }
        val n = info?.bands ?: 0
        val saved = (p.getString("levels", "") ?: "").split(",").mapNotNull { it.toIntOrNull() }
        levels.clear()
        levels.addAll(if (saved.size == n) saved else List(n) { 0 })
        enabled = p.getBoolean("enabled", true)
        attach(0) // intento global: solo funciona en algunos teléfonos
    }

    @Synchronized
    fun attach(session: Int) {
        if (effects.containsKey(session)) return
        try {
            val e = Equalizer(0, session)
            effects[session] = e
            push(e)
            connected = effects.size
        } catch (t: Throwable) { }
    }

    @Synchronized
    fun detach(session: Int) {
        effects.remove(session)?.release()
        connected = effects.size
    }

    private fun push(e: Equalizer) {
        try {
            levels.forEachIndexed { i, l -> e.setBandLevel(i.toShort(), l.toShort()) }
            e.setEnabled(enabled)
        } catch (t: Throwable) { }
    }

    @Synchronized
    fun setLevel(i: Int, mb: Int) {
        levels[i] = mb
        save(); effects.values.forEach { push(it) }
    }

    @Synchronized
    fun setAll(values: List<Int>) {
        values.forEachIndexed { i, v -> if (i < levels.size) levels[i] = v }
        save(); effects.values.forEach { push(it) }
    }

    @Synchronized
    fun setEnabled(v: Boolean) {
        enabled = v
        save(); effects.values.forEach { push(it) }
    }

    private fun save() {
        prefs?.edit()
            ?.putString("levels", levels.joinToString(","))
            ?.putBoolean("enabled", enabled)
            ?.apply()
    }
}
