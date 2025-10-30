package com.example.clubdeportivo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity3 extends AppCompatActivity {

    private DBHelper dbHelper;
    private EditText editTextEmail, editTextPassword;
    private Button btnCrearCuenta, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_cuenta); // Usá tu layout actual

        dbHelper = new DBHelper(this);

        // Referencias a los elementos de la interfaz
        editTextEmail = findViewById(R.id.editTextText2);
        editTextPassword = findViewById(R.id.editTextText5);
        btnCrearCuenta = findViewById(R.id.btnEnviar);
        btnVolver = findViewById(R.id.btnVolver);




        btnCrearCuenta.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String pass = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity3.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("email", email);
            values.put("password", pass);

            long resultado = db.insert("usuarios", null, values);
            db.close();

            if (resultado == -1) {
                Toast.makeText(LoginActivity3.this, "Error: el usuario ya existe o no se pudo registrar", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity3.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        btnVolver.setOnClickListener(v -> {
            finish();
        });
    }
}
