# NUI (Natural User Interfaces) — propuestas para AutiCare (RA2)

Documento informativo que recoge propuestas para incorporar interfaces naturales (NUI) en la aplicación, orientadas a personas que necesitan que un tutor les lleve una organización diaria y a sus tutores. Contiene herramientas potenciales, ventajas, consideraciones de diseño, privacidad y un roadmap de implementación.

Resumen
-------
Las NUI facilitan la interacción reduciendo la dependencia del texto y de precisión motora. Para personas que requieren apoyo en la organización diaria pueden mejorar la accesibilidad, reducir la fricción del inicio de sesión y permitir recordatorios más naturales. Técnicamente, los datos biométricos (huella, cara) se consideran una forma de NUI cuando se usan para autenticación o inicio rápido.

Definición y categorías
-----------------------
NUI incluye técnicas que permiten al usuario interactuar mediante métodos diferentes a la escritura: voz, gestos, biometría, detección facial/gestual, AR, haptics (vibraciones) y UIs conversacionales.

Relevancia para la población objetivo
-------------------------------------
- Menor fricción en acciones frecuentes (inicio de sesión, confirmaciones).
- Interacción simplificada mediante voz y TTS para reducir carga cognitiva.
- Recordatorios multimodales (voz + vibración + visual) que aumentan la probabilidad de respuesta.

Herramientas NUI identificadas y valor esperado
-----------------------------------------------
1. Biometría (`androidx.biometric:biometric` / `BiometricPrompt`)
   - Uso: inicio rápido, desbloqueo de funciones y confirmaciones sensibles.
   - Valor: evita que el usuario recuerde contraseñas.
   - Nota: la plantilla biométrica no se almacena en la app; se delega al sistema.

2. Voz (Speech-to-Text y Text-to-Speech)
   - STT: para comandos simples (p. ej. crear recordatorio).
   - TTS: lectura de instrucciones y confirmaciones.
   - Valor: reduce dependencia visual y motora; útil para guiar rutinas diarias.

3. Interfaces conversacionales
   - Uso: flujos guiados por diálogo para crear tareas/eventos.
   - Valor: guía paso a paso y reducción de errores de entrada.

4. Detección facial / corporal (ML Kit / MediaPipe / TFLite)
   - Uso: detección de atención o estados básicos para adaptar la UI (p. ej. repetir una instrucción si el usuario no presta atención).
   - Consideraciones éticas y de consentimiento detalladas en la sección de privacidad.

5. Reconocimiento de gestos (MediaPipe, sensores)
   - Uso: gestos sencillos para navegación o acciones rápidas.

6. Realidad aumentada (ARCore) — propuestas puntuales
   - Uso: superposición contextual de instrucciones; alta complejidad de implementación.

7. Haptics y feedback multimodal
   - Uso: vibraciones y señales táctiles para confirmar acciones o recordatorios.

8. Componentes predictivos / sugerencias (ML heurístico)
   - Uso: sugerencia de tareas recurrentes y relleno automático de datos.

Requisitos técnicos y dependencias sugeridas
-------------------------------------------
- Biometría: `androidx.biometric:biometric`.
- Voz: `android.speech.SpeechRecognizer` y `TextToSpeech` (local), o servicios en la nube si se dispone de backend.
- Visión/gestos: Google ML Kit (face detection), MediaPipe o TensorFlow Lite.
- Permisos: `RECORD_AUDIO`, `POST_NOTIFICATIONS`, `CAMERA`, `READ_MEDIA_VIDEO` según versión de Android y funcionalidades empleadas.
- Seguridad: uso de Keystore y `EncryptedSharedPreferences` para datos sensibles.

Privacidad y riesgos
--------------------
- Biometría: gestionada por el sistema; no almacenar plantillas en la app.
- Procesamiento de audio e imagen: preferir procesamiento local; cuando se utilice la nube, obtener consentimiento explícito y minimizar datos enviados.

UX y diseño (orientado a personas con apoyo diario)
--------------------------------------------------
- Interfaz simple y consistente: menús reducidos y confirmaciones claras.
- Fallbacks: mantener métodos tradicionales (contraseña) como alternativa.

Medición y validación
---------------------
Métricas propuestas:
- Tiempo medio de inicio de sesión.
- Número de errores en creación de tareas con STT vs UI tradicional.
- Adopción del inicio rápido por biometría.

Pruebas con usuarios
--------------------
- Sesiones de usabilidad cortas con tutores y usuarios asistidos (5–10 minutos) centradas en tareas concretas.
- Métricas mixtas: tiempo, éxito, errores y feedback cualitativo.

Referencias
----------
- Android BiometricPrompt, SpeechRecognizer, TextToSpeech, ML Kit, MediaPipe, TensorFlow Lite.

