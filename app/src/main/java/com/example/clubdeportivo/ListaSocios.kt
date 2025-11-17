package com.example.clubdeportivo

import com.example.clubdeportivo.DBHelper
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.Gravity
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ListaSocios : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var listaSociosLayout: LinearLayout
    private lateinit var btnVolver: MaterialButton
    private lateinit var btnRegistroSocio: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_lista_socios)

        dbHelper = DBHelper(this)

        listaSociosLayout = findViewById(R.id.listaSocios)
        btnVolver = findViewById(R.id.btnCarnet)
        btnRegistroSocio = findViewById(R.id.btnRegistroSocio)

        btnVolver.setOnClickListener {
            finish()
        }

        btnRegistroSocio.setOnClickListener {
            val intent = Intent(this, RegistroSocio::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        mostrarSociosRegistrados()
    }

    /**
     * Carga la lista de strings que devuelve obtenerSociosRegistrados()
     * y la dibuja en el LinearLayout respetando el diseño del layout XML:
     * - círculo con inicial, texto de detalle y checkbox a la derecha.
     */
    private fun mostrarSociosRegistrados() {

        listaSociosLayout.removeAllViews()

        try {
            val socios = dbHelper.obtenerSociosRegistrados()
            if (socios.isEmpty()) {
                val tv = TextView(this).apply {
                    text = "No hay clientes registrados."
                    textSize = 16f
                    gravity = Gravity.CENTER
                    setPadding(0, 24, 0, 24)
                }
                listaSociosLayout.addView(tv)
                return
            }

            val lpMatch = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val outValue = TypedValue()
            theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)

            for (s in socios) {

                val fila = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(0, dpToPx(8), 0, dpToPx(8))
                    layoutParams = lpMatch
                    isClickable = true // Hacemos la fila clicable
                    isFocusable = true
                    setBackgroundResource(outValue.resourceId)
                }

                // Generar inicial (primer carácter del nombre)
                val nombreSolo = extraerNombreDesdeLinea(s) // p. ej. "Juan Gutiérrez"
                val inicial = if (nombreSolo.isNotEmpty()) nombreSolo.trim()[0].toString().uppercase() else "A"

                // TextView circular (reemplaza tu TextView con background circle_background)
                val tvInicial = TextView(this).apply {
                    text = inicial
                    textSize = 18f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    // mismo tamaño aproximado que tu XML (40dp)
                    val sizePx = dpToPx(40)
                    layoutParams = LinearLayout.LayoutParams(sizePx, sizePx)
                    setBackgroundResource(R.drawable.circle_background)
                }

                // TextView con el texto completo (nombre y detalle que devuelve com.example.clubdeportivo.DBHelper)
                val tvNombre = TextView(this).apply {
                    text = s
                    textSize = 16f
                    val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    params.marginStart = dpToPx(12)
                    layoutParams = params
                }

                fila.setOnClickListener {
                    val numeroCarnet = extraerCarnetDesdeLinea(s)
                    if (numeroCarnet != null) {
                        val intent = Intent(this, Carnet::class.java).apply {
                            putExtra("NUMERO_CARNET", numeroCarnet)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Este usuario no tiene un número de carnet válido.", Toast.LENGTH_SHORT).show()
                    }
                }

                fila.addView(tvInicial)
                fila.addView(tvNombre)

                listaSociosLayout.addView(fila)
            }
    } catch (e: Exception) { // <-- AÑADIR CATCH
            // Si algo falla al leer la DB, la app no crasheará.
            // En su lugar, mostrará un mensaje de error.
            Toast.makeText(this, "Error al cargar los datos de socios: ${e.message}", Toast.LENGTH_LONG).show()

            // Opcional: Muestra un texto en la lista para indicar el error
            val tvError = TextView(this).apply {
                text = "Error al cargar la lista."
                textSize = 16f
                gravity = Gravity.CENTER
                setTextColor(Color.RED)
                setPadding(0, 24, 0, 24)
            }
            listaSociosLayout.addView(tvError)

            // Imprime el error en la consola de Logcat para depuración
            e.printStackTrace()
        }
    }

    // --- Helpers para parsear la línea que devuelve obtenerSociosVencidosHoy()
    // formato esperado (por tu com.example.clubdeportivo.DBHelper): "$nombre ($tipo) - Carnet: ${carnet ?: "Sin carnet"} - Vencido"
    private fun extraerCarnetDesdeLinea(linea: String): String? {
        // Buscamos "Carnet:" y tomamos la palabra siguiente hasta espacio o " -"
        val key = "Carnet:"
        val idx = linea.indexOf(key)
        if (idx == -1) return null
        val sub = linea.substring(idx + key.length).trim() // e.g. "C0001 - Vencido" o "Sin carnet - Vencido"
        // Si dice "Sin carnet", devolvemos null
        if (sub.startsWith("Sin carnet", ignoreCase = true)) return null
        // tomamos hasta el primer espacio o " -"
        val parts = sub.split(Regex("\\s|-")).filter { it.isNotBlank() }
        return if (parts.isNotEmpty()) parts[0] else null
    }

    private fun extraerNombreDesdeLinea(linea: String): String {
        // Tomamos todo hasta " (" que precede al tipo según tu formato
        val idx = linea.indexOf(" (")
        return if (idx == -1) {
            // si no encuentra, devolvemos la parte antes de " - Carnet"
            val idx2 = linea.indexOf(" - Carnet")
            if (idx2 == -1) linea else linea.substring(0, idx2)
        } else {
            linea.substring(0, idx)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
