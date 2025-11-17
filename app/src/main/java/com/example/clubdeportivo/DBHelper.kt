package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DBHelper(context: Context): SQLiteOpenHelper(context, "Club.db",null,3) {

    companion object {
        const val TABLE_PAGOS = "pagos"
    }

    override fun onCreate(db: SQLiteDatabase?){
        db!!.execSQL(
            "CREATE TABLE socios(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "numero_carnet TEXT UNIQUE, " +
                    "nombre TEXT NOT NULL, " +
                    "apellido TEXT NOT NULL, " +
                    "dni INTEGER UNIQUE, " +
                    "direccion TEXT NOT NULL, " +
                    "tipo TEXT DEFAULT 'socio', " +
                    "cuota_vencimiento TEXT" +
                    ")"
        )
        db.execSQL(
            """
        CREATE TABLE usuarios(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT UNIQUE,
            password TEXT NOT NULL
        )
        """
        )
        db.execSQL(    "INSERT OR IGNORE INTO usuarios(email, password) VALUES('admin', '1234')"
        )
        db!!.execSQL(
            """
        CREATE TABLE $TABLE_PAGOS (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            fecha_pago TEXT NOT NULL,
            dni_socio INTEGER NOT NULL,
            tipo_socio TEXT NOT NULL,
            monto REAL NOT NULL
        )
        """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int){
        db!!.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PAGOS")
        onCreate(db)
    }

    fun getAllPagos(): List<Map<String, String>> {
        val db = readableDatabase
        val listaPagos = mutableListOf<Map<String, String>>()
        val cursor = db.rawQuery("SELECT fecha_pago, dni_socio, tipo_socio, monto FROM $TABLE_PAGOS ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val dniIndex = cursor.getColumnIndexOrThrow("dni_socio")
                val fechaIndex = cursor.getColumnIndexOrThrow("fecha_pago")
                val tipoIndex = cursor.getColumnIndexOrThrow("tipo_socio")
                val montoIndex = cursor.getColumnIndexOrThrow("monto")

                val dniValue = cursor.getInt(dniIndex).toString()

                val pago = mapOf(
                    "fecha" to cursor.getString(fechaIndex),
                    "dni" to dniValue,
                    "tipo" to cursor.getString(tipoIndex),
                    "monto" to cursor.getDouble(montoIndex).toString()
                )
                listaPagos.add(pago)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return listaPagos
    }
    fun getVencimientosHoyCount(): Int {
        val db = this.readableDatabase
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaHoy = sdf.format(Date())

        val query = "SELECT COUNT(*) FROM socios WHERE cuota_vencimiento = ?"
        val cursor = db.rawQuery(query, arrayOf(fechaHoy))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun getVencimientosSemanaCount(): Int {
        val db = this.readableDatabase
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // Obtener la fecha de hoy
        val calendar = Calendar.getInstance()
        val fechaHoy = sdf.format(calendar.time)

        // Obtener la fecha de fin de semana (hoy + 7 días)
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val fechaFinSemana = sdf.format(calendar.time)

        // Consulta para contar los vencimientos entre hoy y dentro de 7 días
        // El nombre de la columna en tu tabla es "cuota_vencimiento"
        val query = "SELECT COUNT(*) FROM socios WHERE cuota_vencimiento >= ? AND cuota_vencimiento <= ?"
        val cursor = db.rawQuery(query, arrayOf(fechaHoy, fechaFinSemana))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }
    fun validarUsuario(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE email = ? AND password = ?",
            arrayOf(email, password)
        )
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    fun registrarUsuario(email: String, password: String): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("email", email)
            put("password", password)
        }

        return try {
            val resultado = db.insertOrThrow("usuarios", null, values)
            db.close()
            resultado != -1L
        } catch (e: Exception) {
            db.close()
            false
        }
    }

    fun getTipoSocioPorDNI(dni: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT tipo FROM socios WHERE dni = ?", arrayOf(dni))
        var tipo: String? = null
        if (cursor.moveToFirst()) {
            tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
        }
        cursor.close()
        db.close()
        return tipo
    }

    fun renovarCuotaPorDNI(dni: String): Int {
        val db = writableDatabase

        val cursor = db.rawQuery("SELECT tipo FROM socios WHERE dni = ?", arrayOf(dni))
        if (!cursor.moveToFirst()) {
            cursor.close()
            db.close()
            return 0 // No se encontró el DNI
        }

        val tipo = cursor.getString(0)
        cursor.close()

        val calendar = Calendar.getInstance()
        if (tipo == "socio") {
            calendar.add(Calendar.DAY_OF_MONTH, 30)
        } else { // no_socio
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val nuevoVencimiento = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        val values = ContentValues().apply {
            put("cuota_vencimiento", nuevoVencimiento)
        }

        val filasAfectadas = db.update("socios", values, "dni = ?", arrayOf(dni))
        db.close()
        return filasAfectadas
    }

    fun registrarSocio(
        nombre: String,
        apellido: String,
        dni: Int,
        direccion: String,
        fechaVencimiento: String
    ): Long {
        val db = writableDatabase

        val cursor = db.rawQuery("SELECT numero_carnet FROM socios WHERE numero_carnet IS NOT NULL ORDER BY id DESC LIMIT 1", null)
        var nuevoCarnet = "C0001"

        if (cursor.moveToFirst()) {
            val ultimo = cursor.getString(0)
            val numero = ultimo.substring(1).toInt() + 1
            nuevoCarnet = "C" + numero.toString().padStart(4, '0')
        }
        cursor.close()

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1) // ayer
        val vencimiento = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        val values = ContentValues().apply {
            put("numero_carnet", nuevoCarnet)
            put("nombre", nombre)
            put("apellido", apellido)
            put("dni", dni)
            put("direccion", direccion)
            put("tipo", "socio")
            put("cuota_vencimiento", fechaVencimiento)
        }

        val resultado = db.insert("socios", null, values)
        db.close()
        return resultado
    }

    fun registrarNoSocio(nombre: String, apellido: String, dni: Int, direccion: String): Long {
        val db = writableDatabase

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1) // vence al día siguiente
        val vencimiento = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        val values = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("dni", dni)
            put("direccion", direccion)
            put("tipo", "no_socio")
            put("cuota_vencimiento", vencimiento)
        }

        val resultado = db.insert("socios", null, values)
        db.close()
        return resultado
    }

    fun obtenerSocio(): List<String> {
        val db = readableDatabase
        val lista = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT numero_carnet FROM socios", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun renovarCuota(numeroCarnet: String): Int {
        val db = writableDatabase

        val cursor = db.rawQuery("SELECT tipo FROM socios WHERE numero_carnet = ?", arrayOf(numeroCarnet))
        if (!cursor.moveToFirst()) {
            cursor.close()
            db.close()
            return 0
        }

        val tipo = cursor.getString(0)
        cursor.close()

        val calendar = Calendar.getInstance()
        if (tipo == "socio") {
            calendar.add(Calendar.DAY_OF_MONTH, 30)
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val nuevoVencimiento = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        val values = ContentValues().apply {
            put("cuota_vencimiento", nuevoVencimiento)
        }

        val filas = db.update("socios", values, "numero_carnet = ?", arrayOf(numeroCarnet))
        db.close()
        return filas
    }

    fun obtenerSociosVencidosHoy(): List<String> {
        val db = readableDatabase
        val lista = mutableListOf<String>()
        val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val cursor = db.rawQuery(
            "SELECT nombre, apellido, numero_carnet, tipo FROM socios WHERE cuota_vencimiento <= ?",
            arrayOf(hoy)
        )

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(0)
                val apellido = cursor.getString(1)
                val carnet = cursor.getString(2)
                val tipo = cursor.getString(3)
                lista.add("$nombre $apellido ($tipo) - Carnet: ${carnet ?: "Sin carnet"} - Vencido")
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun obtenerSociosRegistrados(): List<String> {
        val db = readableDatabase
        val lista = mutableListOf<String>()

        val cursor = db.rawQuery(
            "SELECT nombre, apellido, numero_carnet, tipo FROM socios", null
        )

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(0)
                val apellido = cursor.getString(1)
                val carnet = cursor.getString(2)
                val tipo = cursor.getString(3)
                lista.add("$nombre $apellido($tipo) - Carnet: ${carnet ?: "Sin carnet"}")
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun obtenerDatosCarnet(numeroCarnet: String): Map<String, String>? {
        val db = readableDatabase
        var datosSocio: Map<String, String>? = null

        val cursor = db.rawQuery(
            "SELECT numero_carnet, nombre, apellido, dni, direccion, tipo FROM socios WHERE numero_carnet = ?", arrayOf(numeroCarnet))

        if (cursor.moveToFirst()) {
            val carnet = cursor.getString(cursor.getColumnIndexOrThrow("numero_carnet"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
            val dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))
            val direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion"))
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))

            datosSocio = mapOf(
                "carnet" to carnet,
                "nombre" to nombre,
                "apellido" to apellido,
                "dni" to dni,
                "direccion" to direccion,
                "tipo" to tipo
            )
        }

        cursor.close()
        db.close()
        return datosSocio
    }

    fun existeDNI(dni: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM socios WHERE dni = ?", arrayOf(dni.toString()))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    fun contarSocios(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM socios WHERE tipo = ?", arrayOf("socio"))
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun contarNoSocios(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM socios WHERE tipo = ?", arrayOf("no_socio"))
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun contarTotalClientes(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM socios", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun registrarPagoYRenovar(dni: Int, monto: Double, fecha: String): Int {
        val db = writableDatabase
        db.beginTransaction()

        var filasAfectadas = 0

        try {
            val cursor = db.rawQuery("SELECT tipo FROM socios WHERE dni = ?", arrayOf(dni.toString()))
            if (!cursor.moveToFirst()) {
                cursor.close()
                return 0
            }

            val tipoSocio = cursor.getString(0)
            cursor.close()

            val pagoValues = ContentValues().apply {
                put("fecha_pago", fecha)
                put("dni_socio", dni)
                put("tipo_socio", tipoSocio)
                put("monto", monto)
            }
            db.insert(TABLE_PAGOS, null, pagoValues)

            val calendar = Calendar.getInstance()
            if (tipoSocio == "socio") {
                calendar.add(Calendar.DAY_OF_MONTH, 30)
            } else { // no_socio
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            val nuevoVencimiento = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            val renovarValues = ContentValues().apply {
                put("cuota_vencimiento", nuevoVencimiento)
            }
            filasAfectadas = db.update("socios", renovarValues, "dni = ?", arrayOf(dni.toString()))

            db.setTransactionSuccessful()

        } finally {
            db.endTransaction()
            db.close()
        }

        return filasAfectadas
    }
}