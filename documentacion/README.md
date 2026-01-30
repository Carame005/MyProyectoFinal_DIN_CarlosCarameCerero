Documentación del proyecto MyProyectoFinal_DIN_CarlosCarameCerero

Carpeta: documentacion/

Contenido creado:
- RA8_plan.md            -> Plan detallado de pruebas RA8 (regresión, estrés, seguridad, uso de recursos)
- tests/stress.ps1       -> Script PowerShell con comandos adb/Monkey para pruebas de estrés
- tests/collect_metrics.ps1 -> Script PowerShell para recolectar métricas (memoria, CPU, batterystats)

Objetivo: proporcionar artefactos reproducibles y ejemplos para evaluar RA8 y generar evidencias.

Instrucciones rápidas:
- Abrir PowerShell y conectar dispositivo/emulador con adb disponible en PATH.
- Ejecutar los scripts en documentacion/tests/ según se indica en cada archivo.

Nota: Los ejemplos de tests Kotlin se incluyen en RA8_plan.md como plantillas; no se han añadido como tests ejecutables dentro de /app/src para evitar romper el build automáticamente.

