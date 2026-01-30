# Script de estrés para MyProyectoFinal
# Requiere adb en PATH y dispositivo/emulador conectado

Write-Host "Iniciando Monkey stress..."
adb shell monkey -p com.example.myproyectofinal_din_carloscaramecerero --throttle 300 -v 10000

Write-Host "Monkey finalizado. Recolecta logs con: adb logcat -d > logcat.txt"

