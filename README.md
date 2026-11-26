# Ecualizador (Android)

1. Abre esta carpeta en Android Studio (Koala o más nuevo) y deja que sincronice Gradle.
2. Conecta tu teléfono con depuración USB y pulsa Run.
3. Abre la app, pon música en Spotify, YouTube Music, etc., y mueve las bandas.

Cómo funciona: la app escucha el aviso OPEN/CLOSE_AUDIO_EFFECT_CONTROL_SESSION que envían los
reproductores y les conecta un `android.media.audiofx.Equalizer`. Además intenta el efecto global
(sesión 0), que solo funciona en algunos teléfonos.
El número de bandas y el rango en dB los define el teléfono (normalmente 5 bandas, ±15 dB).

## Notificación
Al abrir la app (y cuando un reproductor empieza a sonar) aparece una notificación fija con el botón
Activar/Desactivar. En Android 13 o más nuevo, acepta el permiso de notificaciones.

## APK debug
- **Con GitHub:** sube el repo a GitHub (rama `main`). La acción *Build debug APK* compila sola;
  descarga `app-debug.apk` desde Actions > la ejecución > Artifacts.
- **Local:** con Gradle 8.7 instalado, `gradle assembleDebug`
  (o Android Studio: Build > Build APK). Sale en `app/build/outputs/apk/debug/app-debug.apk`.
- Instálalo en el teléfono permitiendo "instalar apps de origen desconocido".
