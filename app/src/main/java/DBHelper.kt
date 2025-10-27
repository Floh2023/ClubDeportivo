import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context): SQLiteOpenHelper(context, "Club.db",null,1) {

    override fun onCreate(db: SQLiteDatabase?){
        db!!.execSQL(
            "CREATE TABLE socios("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "numero_carnet TEXT UNIQUE, "+
                "nombre TEXT NOT NULL, "+
                "dni INTEGER NOT NULL, "+
                "direccion TEXT NOT NULL)"
        )
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int){
        db!!.execSQL("DROP TABLE IF EXISTS socios")
        onCreate(db)
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

        val values = ContentValues().apply {
            put("numero_carnet", nuevoCarnet)
            put("nombre", nombre)
            put("dni", dni)
            put("direccion", direccion)
        }

        val resultado = db.insert("socios", null, values)
        db.close()
        return resultado
    }

    fun obtenerSocio():List<String>{
        val db = readableDatabase
        val lista = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT numero_carnet FROM socios", null)
        if(cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        return lista
    }

}