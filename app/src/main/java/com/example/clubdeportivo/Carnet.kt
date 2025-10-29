package com.example.clubdeportivo

import DBHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Carnet : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_carnet)

        dbHelper = DBHelper(this)

        val btnVolver = findViewById<Button>(R.id.btnCarnet)
        btnVolver.setOnClickListener{
            // val intentarVolver = Intent(this, Opciones::class.java)
            // startActivity(intentarVolver)
            finish()
        }

        // Recibe el número de carnet enviado desde ListaSocios
        val numeroCarnet = intent.getStringExtra("NUMERO_CARNET")

        if (numeroCarnet != null) {
            cargarDatosSocio(numeroCarnet)
        } else {
            // Si no se pasó un número de carnet, mostramos un error y cerramos.
            Toast.makeText(this, "Error: No se proporcionó número de carnet.", Toast.LENGTH_LONG).show()
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun cargarDatosSocio(numeroCarnet: String) {
        // Obtenemos los datos desde la base de datos
        val datos = dbHelper.obtenerDatosCarnet(numeroCarnet)

        if (datos != null) {
            // Referenciamos los TextViews del layout
            val tvNumeroSocio = findViewById<TextView>(R.id.numeroSocio)
            val tvNombre = findViewById<TextView>(R.id.nombre)
            val tvApellido = findViewById<TextView>(R.id.apellido)
            val tvDni = findViewById<TextView>(R.id.dni)
            val tvDireccion = findViewById<TextView>(R.id.direccion)

            // Asignamos los valores a los TextViews
            tvNumeroSocio.text = "Número de socio: ${datos["carnet"]}"

            // Suponemos que el nombre completo está en un solo campo.
            // Si tienes nombre y apellido por separado, debes ajustar la BD y DBHelper.
            tvNombre.text = "Nombre: ${datos["nombre"]}"
            tvApellido.text = "" // Ocultamos o vaciamos el campo apellido si no existe
            tvDni.text = "DNI: ${datos["dni"]}"
            tvDireccion.text = "Dirección: ${datos["direccion"]}"

        } else {
            Toast.makeText(this, "No se encontraron datos para el carnet $numeroCarnet.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}