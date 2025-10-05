package com.example.clubdeportivo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.view.View
import com.google.android.material.card.MaterialCardView

class Opciones : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opciones)

        val registroContainer = findViewById<MaterialCardView>(R.id.cardRegistro)
        registroContainer.setOnClickListener{
            val intentarRegistroSocio = Intent(this, RegistroSocio::class.java)
            startActivity(intentarRegistroSocio)
        }

        val vencimientoContainer = findViewById<MaterialCardView>(R.id.cardVencimientos)
        vencimientoContainer.setOnClickListener{
            val intentarVencimiento = Intent(this, ListaVencimiento::class.java)
            startActivity(intentarVencimiento)
        }

        val pagosContainer = findViewById<MaterialCardView>(R.id.cardPagos)
        pagosContainer.setOnClickListener{
            val intentarPagos = Intent(this, Pago::class.java)
            startActivity(intentarPagos)
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}