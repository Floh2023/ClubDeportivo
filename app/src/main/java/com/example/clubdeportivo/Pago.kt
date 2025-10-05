package com.example.clubdeportivo

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar
import android.app.DatePickerDialog
import android.content.Intent
import android.widget.Button

class Pago : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago)

        val campoFecha = findViewById<EditText>(R.id.campoFecha)
        //val resumenFecha = findViewById<TextView>(R.id.resumenFecha)
        val iconoCalendario = findViewById<ImageView>(R.id.iconoCalendario)

        val calendario = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"
                campoFecha.setText(fechaSeleccionada)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )

        campoFecha.setOnClickListener { datePicker.show() }
        iconoCalendario.setOnClickListener { datePicker.show() }


        val btnVolver = findViewById<Button>(R.id.btnEnviar)
        btnVolver.setOnClickListener{
            val intentarVolver = Intent(this, Opciones::class.java)
            startActivity(intentarVolver)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}