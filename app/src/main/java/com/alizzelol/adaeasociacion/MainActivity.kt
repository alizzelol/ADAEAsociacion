package com.alizzelol.adaeasociacion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnRegistrarse.setOnClickListener { v: View? ->
            val intent = Intent(
                this@MainActivity,
                RegistrarseActivity::class.java
            )
            startActivity(intent)
        }

        btnLogin.setOnClickListener { v: View? ->
            val intent = Intent(
                this@MainActivity,
                LoginActivity::class.java
            )
            startActivity(intent)
        }
    }
}