package com.example.clubdeportivo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity2 extends AppCompatActivity {

    DBHelper dbHelper;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);


        // login
        dbHelper = new DBHelper(this);

        EditText editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        EditText editTextTextPassword = findViewById(R.id.editTextTextPassword);
        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = editTextTextEmailAddress.getText().toString().trim();
                String pass = editTextTextPassword.getText().toString().trim();

                if (usuario.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity2.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                } else if (dbHelper.validarUsuario(usuario, pass)) {
                    Intent intent = new Intent(LoginActivity2.this, Opciones.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity2.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

       /* btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = editTextTextEmailAddress.getText().toString();
                String pass = editTextTextPassword.getText().toString();

                if (usuario.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity2.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                } else if (usuario.equals("admin") && pass.equals("1234")) {
                    Intent intent = new Intent(LoginActivity2.this, Opciones.class); // ejemplo de siguiente pantalla
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity2.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

