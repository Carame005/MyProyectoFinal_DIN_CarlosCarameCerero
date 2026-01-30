# Recolecta métricas básicas: memoria, CPU, batterystats
# Ejecutar con adb conectado

$pkg = "com.example.myproyectofinal_din_carloscaramecerero"
Write-Host "Recolectando meminfo..."
adb shell dumpsys meminfo $pkg > meminfo.txt
Write-Host "Recolectando top snapshot..."
adb shell top -n 1 -b | findstr $pkg > cpu.txt
Write-Host "Recolectando batterystats... (puede tardar)"
adb shell dumpsys batterystats --charged > batterystats.txt
Write-Host "Archivos generados: meminfo.txt, cpu.txt, batterystats.txt"

