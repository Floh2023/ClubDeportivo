package com.example.clubdeportivo

import com.example.clubdeportivo.DBHelper
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Pago : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago)

        // Referencias a los campos del XML
        val dniInput = findViewById<EditText>(R.id.dniInput)
        val montoInput = findViewById<EditText>(R.id.editTextText3)
        val fechaInput = findViewById<EditText>(R.id.campoFecha)
        val btnEnviar = findViewById<MaterialButton>(R.id.btnEnviar)
        val btnVolver = findViewById<MaterialButton>(R.id.btnVolver)
        val iconoCalendario = findViewById<android.widget.ImageView>(R.id.iconoCalendario)
        val tipoSocioText = findViewById<TextView>(R.id.tipo_socio_text)
        val pagoInfoText = findViewById<TextView>(R.id.pago_info_text)

        val dbHelper = DBHelper(this)

        dniInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val dni = s.toString().trim()
                if (dni.isNotEmpty()) {
                    val tipoSocio = dbHelper.getTipoSocioPorDNI(dni)
                    if (tipoSocio != null) {
                        if (tipoSocio == "socio") {
                            tipoSocioText.text = "Socio"
                            pagoInfoText.text = "(para pago mensual)"
                        } else {
                            tipoSocioText.text = "No Socio"
                            pagoInfoText.text = "(para pago diario)"
                        }
                        tipoSocioText.visibility = View.VISIBLE
                        pagoInfoText.visibility = View.VISIBLE // Mostrarlo
                    } else {
                        tipoSocioText.visibility = View.GONE
                        pagoInfoText.visibility = View.GONE // Ocultarlo
                    }
                } else {
                    tipoSocioText.visibility = View.GONE
                    pagoInfoText.visibility = View.GONE // Ocultarlo si el DNI está vacío
                }
            }
        })

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
            val dniString = dniInput.text.toString().trim()
            val montoText = montoInput.text.toString().trim()
            val fecha = fechaInput.text.toString().trim()

            if (dniString.isEmpty() || montoText.isEmpty() || fecha.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Complete todos los campos", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dniInt = dniString.toIntOrNull()
            if (dniInt == null) {
                Snackbar.make(findViewById(android.R.id.content), "DNI inválido.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val monto = montoText.toDoubleOrNull()
            if (monto == null || monto <= 0) {
                Snackbar.make(findViewById(android.R.id.content), "Monto inválido", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultado = dbHelper.registrarPagoYRenovar(dniInt, monto, fecha)

            if (resultado > 0) {
                Snackbar.make(findViewById(android.R.id.content), "Pago registrado correctamente ✅", Snackbar.LENGTH_SHORT).show()

                dniInput.text.clear()
                montoInput.text.clear()
                fechaInput.text.clear()
                tipoSocioText.visibility = View.GONE
                pagoInfoText.visibility = View.GONE
            } else {
                Snackbar.make(findViewById(android.R.id.content), "No se encontró el DNI ingresado. ❌", Snackbar.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}
