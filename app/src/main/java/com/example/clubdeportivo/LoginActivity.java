package com.example.clubdeportivo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnIniciarSesion.setOnClickListener(v -> {
            Intent intentarIniciarSesion = new Intent(LoginActivity.this, LoginActivity2.class);
            startActivity(intentarIniciarSesion);
        });
        Button btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnCrearCuenta.setOnClickListener(v -> {
            Intent intentarCrearCuenta = new Intent(LoginActivity.this, LoginActivity3.class);
            startActivity(intentarCrearCuenta);
        });
    }
}
