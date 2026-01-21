package com.example.myproyectofinal_din_carloscaramecerero.repository

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.myproyectofinal_din_carloscaramecerero.model.CalendarEvent
import com.example.myproyectofinal_din_carloscaramecerero.model.User
import com.example.myproyectofinal_din_carloscaramecerero.model.Task
import com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.VideoCollection
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.VideoItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.time.LocalDate

/**
 * Repositorio de aplicación encargado de guardado/lectura local por usuario.
 *
 * Guarda/lee JSON en archivos privados de la app. Las claves/archivos se derivan del
 * email del usuario (saneado). Provee funciones para:
 *  - credenciales (guardar/leer),
 *  - perfil de usuario (guardar/leer),
 *  - tareas, eventos y colecciones de vídeo (guardar/leer),
 *  - limpieza de datos por usuario o global (pruebas).
 *
 * NOTA: las contraseñas se almacenan en texto plano en ficheros privados (solo para pruebas).
 */
object AppRepository {
    // sufijos de fichero
    private const val SUFFIX_USER = "user.json"
    private const val SUFFIX_TASKS = "tasks.json"
    private const val SUFFIX_EVENTS = "events.json"
    private const val SUFFIX_COLLECTIONS = "collections.json"
    private const val SUFFIX_CREDS = "creds.json" // <-- nuevo sufijo para credenciales

    // helpers para nombre de archivo (sanitizar email)
    /**
     * Construye un nombre de fichero seguro a partir del email del usuario y un sufijo.
     *
     * @param userEmail Email del usuario (se sanea para evitar caracteres no válidos).
     * @param suffix Sufijo que identifica el tipo de dato (ej. "tasks.json").
     * @return Nombre de fichero utilizado internamente.
     */
    private fun fileNameFor(userEmail: String, suffix: String): String {
        val safe = userEmail.replace(Regex("[^A-Za-z0-9_]"), "_")
        return "${safe}_$suffix"
    }

    // --- Credenciales: guardar/leer contraseña simple (no cifrado) ---
    /**
     * Guarda credenciales (contraseña) para el usuario en un fichero privado.
     * @param ctx Contexto.
     * @param userEmail Email del usuario.
     * @param password Contraseña en texto plano (ver nota de seguridad).
     */
    fun saveCredentials(ctx: Context, userEmail: String, password: String) {
        try {
            val jo = JSONObject().apply {
                put("password", password)
            }
            val fn = fileNameFor(userEmail, SUFFIX_CREDS)
            ctx.openFileOutput(fn, Context.MODE_PRIVATE).use { it.write(jo.toString().toByteArray()) }
        } catch (_: Exception) { }
    }

    /**
     * Lee la contraseña guardada para el usuario, o null si no existe.
     * @return contraseña o null.
     */
    fun loadCredentials(ctx: Context, userEmail: String): String? {
        try {
            val fn = fileNameFor(userEmail, SUFFIX_CREDS)
            val text = ctx.openFileInput(fn).bufferedReader().use { it.readText() }
            val jo = JSONObject(text)
            val pw = jo.optString("password", "")
            return if (pw.isBlank()) null else pw
        } catch (ex: FileNotFoundException) {
            return null
        } catch (_: Exception) {
            return null
        }
    }

    /**
     * Busca un usuario por su nombre (case-insensitive) recorriendo los ficheros de usuario
     * almacenados por AppRepository. Útil para aceptar "usuario" en login.
     *
     * @param ctx Contexto.
     * @param name Nombre a buscar.
     * @return User si se encuentra, o null.
     */
    fun findUserByName(ctx: Context, name: String): User? {
        try {
            val files = ctx.fileList()
            for (f in files) {
                if (f.endsWith(SUFFIX_USER)) {
                    try {
                        val text = ctx.openFileInput(f).bufferedReader().use { it.readText() }
                        val jo = JSONObject(text)
                        val n = jo.optString("name", "")
                        if (n.equals(name, ignoreCase = true)) {
                            val email = jo.optString("email", "")
                            val avatarRes = jo.optInt("avatarRes", 0)
                            val avatarUriStr = jo.optString("avatarUri", "")
                            val avatarUri = if (avatarUriStr.isNotBlank()) Uri.parse(avatarUriStr) else null
                            val esAdmin = jo.optBoolean("esAdmin", false)
                            return User(name = n, email = email, avatarRes = avatarRes, avatarUri = avatarUri, esAdmin = esAdmin)
                        }
                    } catch (_: Exception) { /* seguir */ }
                }
            }
        } catch (_: Exception) { }
        return null
    }

    // --- Usuario ---
    /**
     * Persiste el perfil de usuario en un fichero JSON privado.
     *
     * @param ctx Contexto.
     * @param user Perfil a guardar.
     */
    fun saveUser(ctx: Context, user: User) {
        try {
            val jo = JSONObject().apply {
                put("name", user.name)
                put("email", user.email)
                put("avatarRes", user.avatarRes)
                put("avatarUri", user.avatarUri?.toString() ?: "")
                put("esAdmin", user.esAdmin)
            }
            val fn = fileNameFor(user.email, SUFFIX_USER)
            ctx.openFileOutput(fn, Context.MODE_PRIVATE).use { it.write(jo.toString().toByteArray()) }
        } catch (_: Exception) {
            // ignore
        }
    }

    /**
     * Carga el perfil de usuario por email. Devuelve null si no existe.
     */
    fun loadUser(ctx: Context, userEmail: String): User? {
        try {
            val fn = fileNameFor(userEmail, SUFFIX_USER)
            val text = ctx.openFileInput(fn).bufferedReader().use { it.readText() }
            val jo = JSONObject(text)
            val name = jo.optString("name", "Usuario")
            val email = jo.optString("email", userEmail)
            val avatarRes = jo.optInt("avatarRes", 0)
            val avatarUriStr = jo.optString("avatarUri", "")
            val avatarUri = if (avatarUriStr.isNotBlank()) Uri.parse(avatarUriStr) else null
            val esAdmin = jo.optBoolean("esAdmin", false)
            return User(name = name, email = email, avatarRes = avatarRes, avatarUri = avatarUri, esAdmin = esAdmin)
        } catch (ex: FileNotFoundException) {
            return null
        } catch (_: Exception) {
            return null
        }
    }

    // --- Tasks ---
    /**
     * Guarda la lista de tareas del usuario.
     */
    fun saveTasks(ctx: Context, userEmail: String, tasks: List<Task>) {
        try {
            val arr = JSONArray()
            tasks.forEach { t ->
                val jo = JSONObject()
                jo.put("id", t.id)
                jo.put("title", t.title)
                jo.put("description", t.description)
                jo.put("status", t.status.name)
                arr.put(jo)
            }
            val fn = fileNameFor(userEmail, SUFFIX_TASKS)
            ctx.openFileOutput(fn, Context.MODE_PRIVATE).use { it.write(arr.toString().toByteArray()) }
        } catch (_: Exception) { }
    }

    /**
     * Carga las tareas del usuario, devuelve lista vacía si no hay fichero.
     */
    fun loadTasks(ctx: Context, userEmail: String): List<Task> {
        try {
            val fn = fileNameFor(userEmail, SUFFIX_TASKS)
            val text = ctx.openFileInput(fn).bufferedReader().use { it.readText() }
            val arr = JSONArray(text)
            val list = mutableListOf<Task>()
            for (i in 0 until arr.length()) {
                val jo = arr.getJSONObject(i)
                val id = jo.optInt("id", 0)
                val title = jo.optString("title", "")
                val desc = jo.optString("description", "")
                val statusName = jo.optString("status", TaskStatus.PENDING.name)
                val status = try { TaskStatus.valueOf(statusName) } catch (_: Exception) { TaskStatus.PENDING }
                list.add(Task(id = id, title = title, description = desc, status = status))
            }
            return list
        } catch (ex: FileNotFoundException) {
            return emptyList()
        } catch (_: Exception) {
            return emptyList()
        }
    }

    // --- Events (CalendarEvent) ---
    /**
     * Guarda eventos de calendario del usuario.
     */
    fun saveEvents(ctx: Context, userEmail: String, events: List<CalendarEvent>) {
        try {
            val arr = JSONArray()
            events.forEach { e ->
                val jo = JSONObject()
                jo.put("id", e.id)
                jo.put("date", e.date.toString()) // ISO
                jo.put("title", e.title)
                jo.put("time", e.time ?: "")
                arr.put(jo)
            }
            val fn = fileNameFor(userEmail, SUFFIX_EVENTS)
            ctx.openFileOutput(fn, Context.MODE_PRIVATE).use { it.write(arr.toString().toByteArray()) }
        } catch (_: Exception) { }
    }

    /**
     * Carga eventos de calendario del usuario. Usa LocalDate.parse para reconstruir fechas.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadEvents(ctx: Context, userEmail: String): List<CalendarEvent> {
        try {
            val fn = fileNameFor(userEmail, SUFFIX_EVENTS)
            val text = ctx.openFileInput(fn).bufferedReader().use { it.readText() }
            val arr = JSONArray(text)
            val list = mutableListOf<CalendarEvent>()
            for (i in 0 until arr.length()) {
                val jo = arr.getJSONObject(i)
                val id = jo.optInt("id", 0)
                val dateStr = jo.optString("date", "")
                val title = jo.optString("title", "")
                val time = jo.optString("time", "").ifBlank { null }
                val date = try { LocalDate.parse(dateStr) } catch (_: Exception) { null }
                if (date != null) list.add(CalendarEvent(id = id, date = date, title = title, time = time))
            }
            return list
        } catch (ex: FileNotFoundException) {
            return emptyList()
        } catch (_: Exception) {
            return emptyList()
        }
    }

    // --- Video collections ---
    /**
     * Guarda colecciones de vídeo y sus items.
     */
    fun saveCollections(ctx: Context, userEmail: String, collections: List<VideoCollection>) {
        try {
            val arr = JSONArray()
            collections.forEach { c ->
                val cjo = JSONObject()
                cjo.put("id", c.id)
                cjo.put("title", c.title)
                val itemsArr = JSONArray()
                c.items.forEach { itv ->
                    val ivo = JSONObject()
                    ivo.put("id", itv.id)
                    ivo.put("title", itv.title)
                    ivo.put("description", itv.description)
                    ivo.put("uriString", itv.uriString)
                    itemsArr.put(ivo)
                }
                cjo.put("items", itemsArr)
                arr.put(cjo)
            }
            val fn = fileNameFor(userEmail, SUFFIX_COLLECTIONS)
            ctx.openFileOutput(fn, Context.MODE_PRIVATE).use { it.write(arr.toString().toByteArray()) }
        } catch (_: Exception) { }
    }

    /**
     * Carga colecciones de vídeo del usuario, devuelve lista vacía si no existe fichero.
     */
    fun loadCollections(ctx: Context, userEmail: String): List<VideoCollection> {
        try {
            val fn = fileNameFor(userEmail, SUFFIX_COLLECTIONS)
            val text = ctx.openFileInput(fn).bufferedReader().use { it.readText() }
            val arr = JSONArray(text)
            val list = mutableListOf<VideoCollection>()
            for (i in 0 until arr.length()) {
                val cjo = arr.getJSONObject(i)
                val id = cjo.optInt("id", 0)
                val title = cjo.optString("title", "")
                val itemsArr = cjo.optJSONArray("items") ?: JSONArray()
                val items = mutableListOf<VideoItem>()
                for (j in 0 until itemsArr.length()) {
                    val ivo = itemsArr.getJSONObject(j)
                    items.add(
                        VideoItem(
                            id = ivo.optInt("id", 0),
                            title = ivo.optString("title", ""),
                            description = ivo.optString("description", ""),
                            uriString = ivo.optString("uriString", "")
                        )
                    )
                }
                list.add(VideoCollection(id = id, title = title, items = items))
            }
            return list
        } catch (ex: FileNotFoundException) {
            return emptyList()
        } catch (_: Exception) {
            return emptyList()
        }
    }

    // --- Borrar datos de un usuario (logout / limpieza) ---
    /**
     * Elimina todos los ficheros asociados a un usuario (perfil, tareas, eventos, colecciones).
     * Útil en logout para liberar datos del dispositivo.
     */
    fun clearUserData(ctx: Context, userEmail: String) {
        try { ctx.deleteFile(fileNameFor(userEmail, SUFFIX_USER)) } catch (_: Exception) {}
        try { ctx.deleteFile(fileNameFor(userEmail, SUFFIX_TASKS)) } catch (_: Exception) {}
        try { ctx.deleteFile(fileNameFor(userEmail, SUFFIX_EVENTS)) } catch (_: Exception) {}
        try { ctx.deleteFile(fileNameFor(userEmail, SUFFIX_COLLECTIONS)) } catch (_: Exception) {}
    }

    // --- Borrar todos los datos/credenciales del repo (para pruebas) ---
    /**
     * Elimina todos los ficheros gestionados por AppRepository (usuario, tasks, events, collections,
     * credenciales). Diseñado para pruebas: borra también SharedPreferences relacionadas.
     */
    fun clearAllData(ctx: Context) {
        try {
            // borrar ficheros creados por el repo (por sufijo)
            val files = ctx.fileList()
            files.forEach { f ->
                if (f.endsWith(SUFFIX_USER) ||
                    f.endsWith(SUFFIX_TASKS) ||
                    f.endsWith(SUFFIX_EVENTS) ||
                    f.endsWith(SUFFIX_COLLECTIONS) ||
                    f.endsWith(SUFFIX_CREDS)
                ) {
                    try { ctx.deleteFile(f) } catch (_: Exception) {}
                }
            }

            // intentar limpiar SharedPreferences de usuario (user_prefs_*)
            try {
                val spDir = File(ctx.filesDir.parentFile, "shared_prefs")
                if (spDir.exists()) {
                    spDir.listFiles()?.forEach { f ->
                        if (f.name.startsWith("user_prefs_")) {
                            try { f.delete() } catch (_: Exception) {}
                        }
                    }
                }
            } catch (_: Exception) { /* ignore */ }
        } catch (_: Exception) {
            // ignore
        }
    }
}
