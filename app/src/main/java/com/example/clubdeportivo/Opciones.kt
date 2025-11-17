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
import com.google.android.material.button.MaterialButton

class Opciones : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var tvContadorSocios: Button
    private lateinit var tvContadorNoSocios: Button
    private lateinit var tvTotalClientes: Button
    private lateinit var btnRegistrarCliente: Button
    private lateinit var btnVencimientosHoy: MaterialButton
    private lateinit var btnVencimientosSemana: MaterialButton
    private lateinit var btnRegistrarPago: MaterialButton
    private lateinit var btnHistorialPagos: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opciones)

        dbHelper = DBHelper(this)

        tvContadorSocios = findViewById(R.id.btnContadorSocios)
        tvContadorNoSocios = findViewById(R.id.btnContadorNoSocios)
        tvTotalClientes = findViewById(R.id.btnTotalClientes)
        btnRegistrarCliente = findViewById(R.id.btnRegistrarCliente)
        btnVencimientosHoy = findViewById(R.id.btnVencimientosHoy)
        btnVencimientosSemana = findViewById(R.id.btnVencimientosSemana)
        btnRegistrarPago = findViewById(R.id.btnRegistrarPago)
        btnHistorialPagos = findViewById(R.id.btnHistorialPagos)
        btnRegistrarCliente.setOnClickListener {
            val intentarRegistroSocio = Intent(this, RegistroSocio::class.java)
            startActivity(intentarRegistroSocio)
        }

        btnRegistrarPago.setOnClickListener {
            val intentarPago = Intent(this, Pago::class.java)
            startActivity(intentarPago)
        }

        btnHistorialPagos.setOnClickListener {
            val intentarHistorial = Intent(this, ListaPagosActivity::class.java)
            startActivity(intentarHistorial)
        }

        actualizarBotonVencimientos()

        val listaSociosContainer = findViewById<MaterialCardView>(R.id.cardListaSocios)
        listaSociosContainer.setOnClickListener {
            val intentarListaSocios = Intent(this, ListaSocios::class.java)
            startActivity(intentarListaSocios)
        }

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

        Snackbar.make(findViewById(android.R.id.content),
            "Sesión Iniciada..",
            Snackbar.LENGTH_SHORT).show();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarContadores()
        actualizarBotonVencimientos()
    }

    private fun actualizarContadores() {
        val cantidadSocios = dbHelper.contarSocios()
        val cantidadNoSocios = dbHelper.contarNoSocios()
        val totalClientes = dbHelper.contarTotalClientes()

        tvContadorSocios.text = "Socios: $cantidadSocios"
        tvContadorNoSocios.text = "No Socios: $cantidadNoSocios"
        tvTotalClientes.text = "Total de Clientes: $totalClientes"
    }

    private fun actualizarBotonVencimientos() {
        val cantidadVencidos = dbHelper.getVencimientosHoyCount()
        val cantidadVencidosSem = dbHelper.getVencimientosSemanaCount()

        if (cantidadVencidos > 0) {
            btnVencimientosHoy.text = "Hoy: $cantidadVencidos"
        } else {
            btnVencimientosHoy.text = "Hoy"
        }
        if (cantidadVencidosSem > 0) {
            btnVencimientosSemana.text = "Semana: $cantidadVencidosSem"
        } else {
            btnVencimientosSemana.text = "Semana"
        }

    }
}