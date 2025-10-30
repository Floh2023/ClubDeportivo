package com.example.clubdeportivo;

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*


class DBHelper(context: Context): SQLiteOpenHelper(context, "Club.db",null,1) {

    override fun onCreate(db: SQLiteDatabase?){
        db!!.execSQL(
            "CREATE TABLE socios(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "numero_carnet TEXT UNIQUE, " +
                    "nombre TEXT NOT NULL, " +
                    "dni INTEGER NOT NULL, " +
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
        db.execSQL(    "INSERT OR IGNORE INTO usuarios(email, password) VALUES('admin@gmail.com', '1234')"
        )

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int){
        db!!.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
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


    fun registrarSocio(nombre: String, dni: Int, direccion: String): Long {
        val db = writableDatabase

        val cursor = db.rawQuery("SELECT numero_carnet FROM socios ORDER BY id DESC LIMIT 1", null)
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
            put("dni", dni)
            put("direccion", direccion)
            put("tipo", "socio")
            put("cuota_vencimiento", vencimiento)
        }

        val resultado = db.insert("socios", null, values)
        db.close()
        return resultado
    }

    fun registrarNoSocio(nombre: String, dni: Int, direccion: String): Long {
        val db = writableDatabase

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1) // vence al día siguiente
        val vencimiento = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        val values = ContentValues().apply {
            put("nombre", nombre)
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
            "SELECT nombre, numero_carnet, tipo FROM socios WHERE cuota_vencimiento <= ?",
            arrayOf(hoy)
        )

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(0)
                val carnet = cursor.getString(1)
                val tipo = cursor.getString(2)
                lista.add("$nombre ($tipo) - Carnet: ${carnet ?: "Sin carnet"} - Vencido")
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
            "SELECT nombre, numero_carnet, tipo FROM socios", null
        )

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(0)
                val carnet = cursor.getString(1)
                val tipo = cursor.getString(2)
                lista.add("$nombre ($tipo) - Carnet: ${carnet ?: "Sin carnet"}")
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
            "SELECT numero_carnet, nombre, dni, direccion, tipo FROM socios WHERE numero_carnet = ?", arrayOf(numeroCarnet))

        if (cursor.moveToFirst()) {
            val carnet = cursor.getString(cursor.getColumnIndexOrThrow("numero_carnet"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))
            val direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion"))
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))

            datosSocio = mapOf(
                "carnet" to carnet,
                "nombre" to nombre,
                "dni" to dni,
                "direccion" to direccion,
                "tipo" to tipo
            )
        }

        cursor.close()
        db.close()
        return datosSocio
    }
}
