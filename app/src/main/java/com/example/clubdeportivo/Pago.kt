package com.example.clubdeportivo

import com.example.clubdeportivo.DBHelper
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class Pago : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago)

        // Referencias a los campos del XML
        val carnetInput = findViewById<EditText>(R.id.editTextText)
        val montoInput = findViewById<EditText>(R.id.editTextText3)
        val fechaInput = findViewById<EditText>(R.id.campoFecha)
        val btnEnviar = findViewById<MaterialButton>(R.id.btnEnviar)
        val btnVolver = findViewById<MaterialButton>(R.id.btnVolver)
        val iconoCalendario = findViewById<android.widget.ImageView>(R.id.iconoCalendario)

        val dbHelper = DBHelper(this)

        // 📅 Selector de fecha
        val calendario = Calendar.getInstance()
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val abrirCalendario = {
            val año = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val día = calendario.get(Calendar.DAY_OF_MONTH)

            val selector = DatePickerDialog(this, { _, a, m, d ->
                calendario.set(a, m, d)
                fechaInput.setText(formato.format(calendario.time))
            }, año, mes, día)
            selector.show()
        }

        // Abrir calendario al tocar el campo o el ícono
        fechaInput.setOnClickListener { abrirCalendario() }
        iconoCalendario.setOnClickListener { abrirCalendario() }

        // 💾 Acción del botón Registrar
        btnEnviar.setOnClickListener {
            val carnet = carnetInput.text.toString().trim()
            val montoText = montoInput.text.toString().trim()
            val fecha = fechaInput.text.toString().trim()

            if (carnet.isEmpty() || montoText.isEmpty() || fecha.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content),
                    "Complete todos los campos",
                    Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val monto = montoText.toDoubleOrNull()
            if (monto == null || monto <= 0) {
                Snackbar.make(findViewById(android.R.id.content),
                    "Monto inválido",
                    Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Actualizar vencimiento del socio en la base de datos
            val resultado = dbHelper.renovarCuota(carnet)

            if (resultado > 0) {
                Snackbar.make(findViewById(android.R.id.content),
                    "Pago registrado correctamente ✅",
                    Snackbar.LENGTH_SHORT).show()

                carnetInput.text.clear()
                montoInput.text.clear()
                fechaInput.text.clear()
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                    "No se encontró el carnet ingresado ❌",
                    Snackbar.LENGTH_SHORT).show()
            }
        }
        // 🔹 Botón para volver a la actividad anterior
        btnVolver.setOnClickListener {
            finish()
        }
    }
}
