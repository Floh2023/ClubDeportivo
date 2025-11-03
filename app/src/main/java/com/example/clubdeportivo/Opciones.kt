package com.example.clubdeportivo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar

class Opciones : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opciones)

        dbHelper = DBHelper(this)

        val listaSociosContainer = findViewById<MaterialCardView>(R.id.cardListaSocios)
        val tvContadorSocios = findViewById<TextView>(R.id.tvContadorSocios)
        val tvContadorNoSocios = findViewById<TextView>(R.id.tvContadorNoSocios)
        val tvTotalClientes = findViewById<TextView>(R.id.tvTotalClientes)

        val cantidadSocios = dbHelper.contarSocios()
        val cantidadNoSocios = dbHelper.contarNoSocios()
        val totalClientes = dbHelper.contarTotalClientes()

        tvContadorSocios.text = "Socios: $cantidadSocios"
        tvContadorNoSocios.text = "No Socios: $cantidadNoSocios"
        tvTotalClientes.text = "Total de Clientes: $totalClientes"

        listaSociosContainer.setOnClickListener {
            val intentarListaSocios = Intent(this, ListaSocios::class.java)
            startActivity(intentarListaSocios)
        }

        //val listaSociosContainer = findViewById<MaterialCardView>(R.id.cardListaSocios)
        //listaSociosContainer.setOnClickListener{
            //val intentarListaSocios = Intent(this, ListaSocios::class.java)
            //startActivity(intentarListaSocios)
        //}

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

        val usuario = intent.getStringExtra("usuario") ?: "Usuario"

        Snackbar.make(findViewById(android.R.id.content),
            "Sesión Iniciada..",
            Snackbar.LENGTH_SHORT).show();

        //Aun  no esta implementado el botton de cerrar sesion

        /*val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)
        btnCerrarSesion.setOnClickListener{
            AletDialog.Builder(this)
                .setTitle("Cerrar Sesion")
                .setMessage("¿Queres cerrar la sesión?")
                .setPositiveButton("si"){_,_ ->
                    finish()
                }
                .setNegativeButton("no",null)
                .show()

        }*/


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}