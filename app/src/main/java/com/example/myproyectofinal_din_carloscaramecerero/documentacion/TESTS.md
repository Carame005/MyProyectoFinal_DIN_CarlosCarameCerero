# Pruebas (Tests)

Estado actual
-------------
- Hay un test unitario de ejemplo: `app/src/test/.../ExampleUnitTest.kt` (assertEquals(4, 2 + 2)).
- Hay un test instrumentado de ejemplo: `app/src/androidTest/.../ExampleInstrumentedTest.kt` que comprueba el packageName.

Estrategia recomendada
----------------------
1. Añadir tests unitarios para `AppRepository` (serialización/deserialización de JSON) usando `Robolectric` o mocking de `Context`.
2. Añadir tests para la lógica de `LoginScreen` y `AppRepository` (registro/login) comprobando migración de credenciales.
3. Añadir pruebas instrumentadas (o end-to-end) para flujos críticos: login, añadir tarea, crear evento y reproducir vídeo.
4. Añadir un workflow CI que ejecute `./gradlew build` y `./gradlew test` en cada PR.

Ejemplos de pruebas sugeridas
----------------------------
- Serializar y deserializar `Task` y `CalendarEvent` y comprobar igualdad estructural.
- Guardar y cargar usuario en un contexto temporal (mock) y comprobar que los campos persisten.

Herramientas
-----------
- JUnit 4/5, Robolectric, MockK/Mockito para mocking, Espresso para UI testeos instrumentados.
