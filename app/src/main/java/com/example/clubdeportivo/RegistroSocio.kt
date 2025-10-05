package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton

class RegistroSocio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_socio)



        val btnCarnet = findViewById<Button>(R.id.btnCarnet)
        btnCarnet.setOnClickListener{
            val intentarCarnet = Intent(this, Carnet::class.java)
            startActivity(intentarCarnet)
        }

        val btnRegistrar = findViewById<Button>(R.id.btnEnviar)
        btnRegistrar.setOnClickListener {
            mostrarToastPersonalizado()
        }

        val iconoCerrar = findViewById<ImageView>(R.id.iconoCerrar2)

        iconoCerrar.setOnClickListener {
            val intent = Intent(this, Opciones::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun mostrarToastPersonalizado() {
        val layout = layoutInflater.inflate(R.layout.toast_registro, null)
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.setGravity(Gravity.CENTER, 0, 0)

        val btnAceptar = layout.findViewById<AppCompatButton>(R.id.btnAceptar)
        val btnCancelar = layout.findViewById<AppCompatButton>(R.id.btnCarnet)

        btnAceptar.setOnClickListener {
            toast.cancel()
        }

        btnCancelar.setOnClickListener {
            toast.cancel()
        }

        toast.show()
    }

}
