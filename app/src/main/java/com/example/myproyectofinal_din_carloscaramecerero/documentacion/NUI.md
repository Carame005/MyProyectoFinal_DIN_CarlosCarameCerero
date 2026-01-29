# NUI (Natural User Interfaces) — propuestas para AutiCare

Este documento recoge ideas y recomendaciones para incorporar interfaces naturales (NUI) en la aplicación, con foco en personas con Alzheimer y sus cuidadores. Incluye las herramientas posibles, ventajas para el usuario objetivo, consideraciones de diseño, privacidad y un roadmap de implementación.

Resumen rápido
---------------
- Las NUI facilitan la interacción reduciendo la dependencia del texto y de precisión motora. Para usuarios con Alzheimer pueden mejorar la accesibilidad, reducir la fricción del login, y permitir recordatorios más naturales.
- Técnicamente, los datos biométricos (huella, cara) se consideran una forma de NUI cuando se usan para autenticación/entrada rápida.

Qué entendemos por NUI
----------------------
NUI agrupa técnicas que permiten a la persona interactuar con la app mediante métodos más naturales que teclear: voz, gestos, biometría, detección facial/gestual, realidad aumentada (AR), haptics (vibraciones contextuales) y UIs conversacionales.

Relevancia para personas con Alzheimer
--------------------------------------
- Menos fricción: reducir o eliminar la necesidad de recordar contraseñas (inicio rápido biométrico, autenticación por cuidador, inicio por selección de perfil + biometría).
- Interacción sencilla: comandos de voz e indicaciones habladas (TTS) permiten operar sin recordar rutas o pulir puntería táctil.
- Recordatorios más naturales: mensajes hablados, repeticiones y confirmaciones por voz.
- Seguridad y control: biometría y consentimiento del cuidador para permisos críticos.

Herramientas NUI relevantes y cómo aportarían valor
---------------------------------------------------
1. Biometría (AndroidX Biometric / BiometricPrompt)
   - Uso: inicio rápido, desbloqueo de funciones, confirmaciones sensibles (p. ej. eliminar datos).
   - Valor: evita que el usuario recuerde contraseñas; arranque más seguro y cómodo.
   - Observación: los datos biométricos no deben almacenarse por la app; usar el sistema (Keystore/Android Biometric API).

2. Voz (Speech-to-Text y Text-to-Speech)
   - STT: para comandos simples ("añadir tarea", "recordarme a las 9").
   - TTS: lectura de instrucciones, confirmar acciones, repetir recordatorios.
   - Valor: reduce dependencia visual y motora; buen soporte para usuarios con deterioro cognitivo.
   - Tecnologías: Android SpeechRecognizer (offline/online), Google Cloud Speech (si hay backend), Android TextToSpeech o TTS de Google.

3. Interfaces conversacionales (bot conversacional simple)
   - Uso: flujo guiado por diálogo para crear tareas o eventos (p. ej. agente que pregunta "¿qué quieres recordar?").
   - Valor: guía paso a paso; reduce errores de entrada.

4. Detección facial y de estado (ML Kit / MediaPipe / TensorFlow Lite)
   - Uso: detectar atención (si la persona mira la pantalla) o emociones básicas (para adaptar mensajes) y para perfiles con foto; ayuda al cuidador.
   - Valor: adaptar la experiencia según el estado del usuario (p. ej. simplificar la UI si el usuario está estresado).
   - Consideraciones éticas: detección de emociones es sensible — usar con consentimiento explícito y mínimos datos.

5. Reconocimiento de gestos (MediaPipe, sensores, acelerómetro)
   - Uso: gestos sencillos para controlar pantallas (p. ej. pasar al siguiente elemento con gesto de mano) o atajos físicos.
   - Valor: alternativa a toques pequeños; útil en tablets con cámara o dispositivos con sensores.

6. Realidad aumentada (ARCore) — propuestas ligeras
   - Uso: superponer instrucciones grandes en objetos reales (por ejemplo recordar dónde está la medicación), marcadores visuales para contextualizar tareas.
   - Valor: experiencia inmersiva y contextual, pero alto coste de implementación.

7. Haptics y feedback multimodal
   - Uso: vibraciones sutiles para confirmar una acción o para llamar la atención en recordatorios.
   - Valor: útil cuando audio no puede escucharse o para reforzar confirmaciones visuales.

8. Componentes predictivos / sugerencias (ML o heurísticos)
   - Uso: sugerir tareas recurrentes, rellenado automático de horas frecuentes, recordatorios inteligentes basados en rutina.
   - Valor: reduce necesidad de entrar datos manualmente.

Requisitos técnicos y bibliotecas sugeridas
-------------------------------------------
- Biometría: `androidx.biometric:biometric` (usar la versión estable disponible). Usar `BiometricPrompt` (no almacenar biométricos en la app).
- Voz: Android SpeechRecognizer, `android.speech.tts.TextToSpeech`; para mayor fiabilidad, evaluar servicios en la nube (requiere backend y consentimiento).  
- Visión / gestos: Google ML Kit (face detection), MediaPipe (gestures) o TensorFlow Lite para modelos custom.
- Permisos: RECORD_AUDIO (STT), POST_NOTIFICATIONS (notifs), CAMERA (gestos / face), READ_EXTERNAL_STORAGE / READ_MEDIA_VIDEO (según Android), uso de `takePersistableUriPermission` para URIs persistentes.
- Seguridad: Android Keystore, EncryptedSharedPreferences para datos sensibles, no almacenar biométricos.

Consideraciones de privacidad y seguridad
-----------------------------------------
- La biometría se gestiona siempre por el sistema; nunca exportar o almacenar plantillas biométricas.
- Pedir consentimiento claro y registro por parte del cuidador para funcionalidades que procesen imagen/audio.
- Registrar y controlar los permisos; proporcionar opción para revocar fácilmente y un modo "solo cuidador".
- Logs y datos sensibles deben cifrarse (EncryptedSharedPreferences / Keystore); minimizar datos personales enviados a la nube.

UX y diseño (guías específicas para Alzheimer)
---------------------------------------------
- Simplicidad: comandos limitados y repetibles; evitar menús profundos.
- Confirmación multimodal: combinar TTS + vibración + UI para confirmar acciones importantes.
- Repetición y refuerzo: read-out loud de recordatorios y opción para repetir.
- Fallbacks: si STT falla, ofrecer picker visual simple; si biometría no disponible, fallback a selección de cuenta + contraseña.
- Consentimiento y control del cuidador: permitir que el cuidador registre la biometría o active modos asistidos.
- Evitar automatismos peligrosos: no permitir borrar datos críticos sin confirmación del cuidador.

Riesgos y mitigaciones
-----------------------
- Falsos positivos/negativos (STT/face/gestos): usar confirmaciones y permitir deshacer.
- Privacidad (audio/imágenes): procesar localmente cuando sea posible; si se usa la nube, cifrar y pedir consentimiento.
- Dependencia excesiva: no eliminar completamente los flujos tradicionales (contraseña) — ofrecer siempre un fallback.

Cómo medir éxito (KPIs)
-----------------------
- Reducción en tiempo para iniciar sesión (medir antes/después con usuarios).
- Número de errores al crear/usar tareas con STT vs UI tradicional.
- Tasa de adopción del inicio rápido por biometría.
- Feedback cualitativo de cuidadores y usuarios (test de usabilidad con tareas concretas).

Pruebas y validación con usuarios
---------------------------------
- Testear con cuidadores y usuarios objetivo en sesiones cortas (5–10 minutos) con tareas concretas.
- Usar métricas mixtas: tiempo, éxito, errores, y escala de usabilidad adaptada (SUS simplificado + feedback cualitativo).

Referencias y bibliografía
--------------------------
- Android BiometricPrompt (AndroidX Biometric)
- Android SpeechRecognizer y TextToSpeech
- Google ML Kit (Face Detection)
- MediaPipe (gestures)
- TensorFlow Lite (models locales)
- Buenas prácticas de privacidad biométrica (documentos regulatorios locales/UE)

