package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper

class PreviewActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preview)

        Handler(Looper.getMainLooper()).postDelayed({

            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)

            finish()
        }, SPLASH_TIME_OUT)
    }
}