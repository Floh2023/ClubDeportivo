package com.example.clubdeportivo

import com.example.clubdeportivo.DBHelper
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ListaVencimiento : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var listaVencimientosLayout: LinearLayout
    private lateinit var btnVolver: MaterialButton
    private lateinit var btnEnviar: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Reemplazá por el nombre real del layout si no es activity_lista_vencimiento
        setContentView(R.layout.activity_lista_vencimiento)

        dbHelper = DBHelper(this)

        // IDs tal como aparecen en tu XML
        listaVencimientosLayout = findViewById(R.id.listaVencimientos)
        btnVolver = findViewById(R.id.btnCarnet)
        btnEnviar = findViewById(R.id.btnEnviar)

        // Cargar lista desde la DB y mostrar
        mostrarSociosVencidos()

        btnVolver.setOnClickListener {
            // Simplemente volvemos a la actividad anterior
            finish()
        }

        btnEnviar.setOnClickListener {
            enviarMailSeleccionados()
        }
    }

    /**
     * Carga la lista de strings que devuelve obtenerSociosVencidosHoy()
     * y la dibuja en el LinearLayout respetando el diseño del layout XML:
     * - círculo con inicial, texto de detalle y checkbox a la derecha.
     */
    private fun mostrarSociosVencidos() {
        // Primero limpiamos lo que hubiera (tu XML tiene ítems de ejemplo; los removemos)
        listaVencimientosLayout.removeAllViews()

        val socios = dbHelper.obtenerSociosVencidosHoy()
        if (socios.isEmpty()) {
            val tv = TextView(this).apply {
                text = "No hay socios con cuota vencida."
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(0, 24, 0, 24)
            }
            listaVencimientosLayout.addView(tv)
            return
        }

        val lpMatch = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        for (s in socios) {
            // Layout horizontal por fila (igual que en tu XML)
            val fila = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dpToPx(8), 0, dpToPx(8))
                layoutParams = lpMatch
            }

            // Generar inicial (primer carácter del nombre)
            val nombreSolo = extraerNombreDesdeLinea(s) // p. ej. "Juan Gutiérrez"
            val inicial = if (nombreSolo.isNotEmpty()) nombreSolo.trim()[0].toString().uppercase() else "A"

            // TextView circular (reemplaza tu TextView con background circle_background)
            val tvInicial = TextView(this).apply {
                text = inicial
                textSize = 18f
                setTypeface(null, Typeface.BOLD)
                setTextColor(android.graphics.Color.WHITE)
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

            // Checkbox
            val cb = CheckBox(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                // guardamos el número de carnet (si existe) en tag para usarlo después; puede ser null
                val carnet = extraerCarnetDesdeLinea(s) // p.ej. "C0001" o null
                tag = carnet ?: s // si no hay carnet, guardo la línea completa
            }

            fila.addView(tvInicial)
            fila.addView(tvNombre)
            fila.addView(cb)

            listaVencimientosLayout.addView(fila)
        }
    }

    /**
     * Recolecta los elementos seleccionados y abre un Intent chooser para enviar un mail.
     * Como tu tabla no tiene emails, armamos un cuerpo de mail con los nombres/carnets seleccionados.
     */
    private fun enviarMailSeleccionados() {
        val seleccionados = mutableListOf<String>()
        val carnetsSeleccionados = mutableListOf<String>()

        for (i in 0 until listaVencimientosLayout.childCount) {
            val fila = listaVencimientosLayout.getChildAt(i)
            if (fila is LinearLayout && fila.childCount >= 3) {
                val tv = fila.getChildAt(1)
                val cb = fila.getChildAt(2)
                if (tv is TextView && cb is CheckBox && cb.isChecked) {
                    seleccionados.add(tv.text.toString())
                    val tag = cb.tag
                    carnetsSeleccionados.add(tag?.toString() ?: "")
                }
            }
        }

        if (seleccionados.isEmpty()) {
            Toast.makeText(this, "No seleccionaste ningún socio.", Toast.LENGTH_SHORT).show()
            return
        }

        // Cuerpo del mail
        val cuerpo = StringBuilder()
        cuerpo.append("Estimado/a,\n\nSe le informa que su cuota se encuentra vencida. Detalle:\n\n")
        seleccionados.forEach {
            cuerpo.append("- $it\n")
        }
        cuerpo.append("\nPor favor regularice a la brevedad.\n\nClub Deportivo")

        // Intent genérico de correo (no tenemos e-mails en la DB)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_SUBJECT, "Aviso: cuota vencida")
            putExtra(Intent.EXTRA_TEXT, cuerpo.toString())
            // No ponemos EXTRA_EMAIL porque no hay emails en la tabla; si agregás emails podrías mapearlos aquí
        }

        // Si querés, podés abrir un dialog con la lista de carnets seleccionados o guardarlos para marcar renovación automática.
        startActivity(Intent.createChooser(intent, "Enviar mail con..."))
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
