package com.example.clubdeportivo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ListaPagosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var pagosAdapter: PagosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_pagos)

        dbHelper = DBHelper(this)
        recyclerView = findViewById(R.id.recyclerViewPagos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val listaPagos = dbHelper.getAllPagos()
        if (listaPagos.isNotEmpty()) {
            pagosAdapter = PagosAdapter(listaPagos)
            recyclerView.adapter = pagosAdapter
        }

        val btnVolver: MaterialButton = findViewById(R.id.btnVolverPagos)
        btnVolver.setOnClickListener {
            finish()
        }
    }
}
