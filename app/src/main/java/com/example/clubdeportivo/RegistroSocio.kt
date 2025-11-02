package com.example.clubdeportivo

import com.example.clubdeportivo.DBHelper
import DBHelper
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class RegistroSocio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_socio)

        val dbHelper = DBHelper(this)

        val nombreInput = findViewById<EditText>(R.id.editTextText4)
        val apellidoInput = findViewById< android . widget . EditText>(R.id.editTextApellido)
        val dniInput = findViewById<EditText>(R.id.editTextText2)
        val direccionInput = findViewById<EditText>(R.id.editTextText5)
        val btnEnviar = findViewById<MaterialButton>(R.id.btnEnviar)
        val btnVolver = findViewById<MaterialButton>(R.id.btnVolver)
        val switchSocio = findViewById<SwitchCompat>(R.id.switchSocio)
        val labelSocio = findViewById<TextView>(R.id.labelSocio)
        val labelNoSocio = findViewById<TextView>(R.id.labelNoSocio)

        labelNoSocio.setTypeface(null, Typeface.BOLD)
        labelNoSocio.setTextColor(Color.parseColor("#F44336"))
        labelSocio.setTypeface(null, Typeface.NORMAL)
        labelSocio.setTextColor(Color.GRAY)

        switchSocio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Estado: Socio (Activado)
                labelSocio.setTypeface(null, Typeface.BOLD)
                labelSocio.setTextColor(Color.parseColor("#4CAF50")) // Verde
                labelNoSocio.setTypeface(null, Typeface.NORMAL)
                labelNoSocio.setTextColor(Color.GRAY)
            } else {
                // Estado: No Socio (Desactivado)
                labelNoSocio.setTypeface(null, Typeface.BOLD)
                labelNoSocio.setTextColor(Color.parseColor("#F44336")) // Rojo
                labelSocio.setTypeface(null, Typeface.NORMAL)
                labelSocio.setTextColor(Color.GRAY)
            }
        }

        // 🔹 Botón para registrar socio nuevo
        btnEnviar.setOnClickListener {
            val nombre = nombreInput.text.toString().trim()
            val apellido = apellidoInput.text.toString().trim()
            val dniText = dniInput.text.toString().trim()
            val direccion = direccionInput.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || dniText.isEmpty() || direccion.isEmpty()) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Complete todos los campos",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val dni = dniText.toIntOrNull()
            if (dni == null) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "El DNI debe ser numérico",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (dbHelper.existeDNI(dni)) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "El DNI ingresado ya se encuentra registrado",
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val resultado: Long
            val mensajeExito: String

            if (switchSocio.isChecked) {
                resultado = dbHelper.registrarSocio(nombre, apellido, dni, direccion)
                mensajeExito = "Socio registrado correctamente"
            } else {
                resultado = dbHelper.registrarNoSocio(nombre, apellido, dni, direccion)
                mensajeExito = "No socio registrado correctamente"
            }

            // Registrar socio con carnet y vencimiento mensual
            // val resultado = dbHelper.registrarSocio(nombre, dni, direccion)
            if (resultado > 0) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    mensajeExito,
                    Snackbar.LENGTH_SHORT
                ).show()
                nombreInput.text.clear()
                apellidoInput.text.clear()
                dniInput.text.clear()
                direccionInput.text.clear()
            } else {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Error al registrar socio",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        // 🔹 Botón para volver a la actividad anterior
        btnVolver.setOnClickListener {
            finish()
        }
    }

}
