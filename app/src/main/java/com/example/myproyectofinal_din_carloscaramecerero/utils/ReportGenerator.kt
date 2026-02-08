package com.example.myproyectofinal_din_carloscaramecerero.utils

import android.content.Context
import android.os.Build
import android.annotation.SuppressLint
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.example.myproyectofinal_din_carloscaramecerero.model.ReportFilters
import com.example.myproyectofinal_din_carloscaramecerero.model.ReportPeriod
import com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ReportGenerator {

    private fun periodStart(period: ReportPeriod): LocalDate {
        val today = LocalDate.now()
        return when (period) {
            ReportPeriod.LAST_WEEK -> today.minusDays(7)
            ReportPeriod.LAST_MONTH -> today.minusDays(30)
        }
    }

    @Suppress("NewApi")
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    fun buildReportText(context: Context, targetEmail: String, filters: ReportFilters): String {
        val from = periodStart(filters.period)
        val now = LocalDate.now()

        val tasks = AppRepository.loadTasks(context, targetEmail)
        val events = AppRepository.loadEvents(context, targetEmail)
            .filter { it.date >= from && it.date <= now }
        val collections = AppRepository.loadCollections(context, targetEmail)

        // contadores de tareas
        val totalTasks = tasks.size
        val completedCount = tasks.count { it.status == TaskStatus.DONE }
        val inProgressCount = tasks.count { it.status == TaskStatus.IN_PROGRESS }
        val pendingCount = tasks.count { it.status == TaskStatus.PENDING }

        // tareas filtradas según filtros
        val tasksFiltered = tasks.filter { t ->
            when (t.status) {
                TaskStatus.DONE -> filters.includeCompleted
                TaskStatus.IN_PROGRESS -> filters.includeInProgress
                else -> filters.includePending
            }
        }

        // vídeos: recuentos por colección y total
        val collectionsCount = collections.size
        val videosByCollection = collections.map { col -> Pair(col.title, col.items.size) }
        val totalVideos = videosByCollection.fold(0) { acc, p -> acc + p.second }

        val sb = StringBuilder()
        val fmt = DateTimeFormatter.ISO_LOCAL_DATE
        sb.appendLine("Informe TutorOrganiza")
        sb.appendLine("Usuario: $targetEmail")
        sb.appendLine("Periodo: ${filters.period}")
        sb.appendLine("Generado: ${fmt.format(now)}")
        sb.appendLine()

        sb.appendLine("Resumen numérico:")
        // incluir sólo las secciones numericas solicitadas por los filtros
        if (filters.includeCompleted || filters.includeInProgress || filters.includePending) {
            sb.appendLine("- Tareas: $totalTasks")
            if (filters.includeCompleted) sb.appendLine("  - Completadas: $completedCount")
            if (filters.includeInProgress) sb.appendLine("  - En proceso: $inProgressCount")
            if (filters.includePending) sb.appendLine("  - Pendientes: $pendingCount")
        }
        if (filters.includeEvents) {
            sb.appendLine("- Eventos: ${events.size}")
        }
        if (filters.includeVideos) {
            sb.appendLine("- Colecciones: $collectionsCount (total vídeos: $totalVideos)")
        }

        // Si no se seleccionó nada, avisar
        if (!filters.includeCompleted && !filters.includeInProgress && !filters.includePending && !filters.includeEvents && !filters.includeVideos) {
            sb.appendLine("(No se incluyó ningún tipo de dato en el informe.)")
            sb.appendLine()
            return sb.toString()
        }

        sb.appendLine()

        if (filters.includeCompleted || filters.includeInProgress || filters.includePending) {
            sb.appendLine("Tareas:")
            if (tasksFiltered.isEmpty()) {
                sb.appendLine("No hay tareas")
            } else {
                tasksFiltered.forEach { t ->
                    // usar la descripción legible del estado (TaskStatus.desc)
                    sb.appendLine("- [${t.status.desc}] ${t.title} (id=${t.id})")
                    if (t.description.isNotBlank()) sb.appendLine("  Desc: ${t.description}")
                }
            }
            sb.appendLine()
        }

        if (filters.includeEvents) {
            sb.appendLine("Eventos:")
            if (events.isEmpty()) {
                sb.appendLine("No hay eventos")
            } else {
                for (e in events) {
                    val timePart = if (e.time.isNullOrBlank()) "" else " ${e.time}"
                    sb.appendLine("- ${e.title} — ${e.date}$timePart (id=${e.id})")
                }
            }
            sb.appendLine()
        }

        if (filters.includeVideos) {
            sb.appendLine("Videos / colecciones:")
            if (totalVideos == 0) {
                sb.appendLine("No hay videos")
            } else {
                for ((title, count) in videosByCollection) {
                    sb.appendLine("- Colección: $title ($count videos)")
                }
                sb.appendLine("Total de vídeos añadidos: $totalVideos")
            }
            sb.appendLine()
        }

        return sb.toString()
    }

    /**
     * Genera un resumen cuantitativo del informe (para gráficos) sin generar el texto.
     */
    @Suppress("NewApi")
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    fun buildReportSummary(context: Context, targetEmail: String, filters: ReportFilters): com.example.myproyectofinal_din_carloscaramecerero.model.ReportSummary {
        val from = periodStart(filters.period)
        val now = LocalDate.now()

        val tasks = AppRepository.loadTasks(context, targetEmail)
        val events = AppRepository.loadEvents(context, targetEmail).filter { it.date >= from && it.date <= now }
        val collections = AppRepository.loadCollections(context, targetEmail)

        val completed = tasks.count { it.status == com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus.DONE }
        val inProgress = tasks.count { it.status == com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus.IN_PROGRESS }
        val pending = tasks.count { it.status == com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus.PENDING }
        val totalVideos = collections.sumOf { it.items.size }

        return com.example.myproyectofinal_din_carloscaramecerero.model.ReportSummary(
            tasksCompleted = if (filters.includeCompleted) completed else 0,
            tasksInProgress = if (filters.includeInProgress) inProgress else 0,
            tasksPending = if (filters.includePending) pending else 0,
            eventsCount = if (filters.includeEvents) events.size else 0,
            totalVideos = if (filters.includeVideos) totalVideos else 0
        )
    }

    fun saveReportToCache(context: Context, filename: String, content: String): String? {
        return try {
            val file = File(context.cacheDir, filename)
            file.writeText(content)
            file.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    fun getUriForFile(context: Context, absolutePath: String): android.net.Uri {
        val file = File(absolutePath)
        val authority = context.packageName + ".fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

}
