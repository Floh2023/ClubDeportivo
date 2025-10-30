package com.example.clubdeportivo

import com.example.clubdeportivo.DBHelper
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class RegistroSocio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_socio)

        val dbHelper = DBHelper(this)

        val nombreInput = findViewById<EditText>(R.id.editTextText4)
        val dniInput = findViewById<EditText>(R.id.editTextText2)
        val direccionInput = findViewById<EditText>(R.id.editTextText5)
        val btnEnviar = findViewById<MaterialButton>(R.id.btnEnviar)
        val btnCarnet = findViewById<MaterialButton>(R.id.btnCarnet)
        val btnVolver = findViewById<MaterialButton>(R.id.btnVolver)

        // 🔹 Botón para mostrar los carnets registrados
        btnCarnet.setOnClickListener {
            val lista = dbHelper.obtenerSocio()
            val mensaje = if (lista.isEmpty()) {
                "No hay carnets registrados"
            } else {
                "Carnets registrados:\n${lista.joinToString(", ")}"
            }
            Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_LONG).show()
        }

        // 🔹 Botón para registrar socio nuevo
        btnEnviar.setOnClickListener {
            val nombre = nombreInput.text.toString().trim()
            val dniText = dniInput.text.toString().trim()
            val direccion = direccionInput.text.toString().trim()

            if (nombre.isEmpty() || dniText.isEmpty() || direccion.isEmpty()) {
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

            // Registrar socio con carnet y vencimiento mensual
            val resultado = dbHelper.registrarSocio(nombre, dni, direccion)
            if (resultado > 0) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Socio registrado correctamente",
                    Snackbar.LENGTH_SHORT
                ).show()
                nombreInput.text.clear()
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
