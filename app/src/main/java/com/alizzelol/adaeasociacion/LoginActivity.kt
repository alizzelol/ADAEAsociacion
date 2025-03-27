package com.alizzelol.adaeasociacion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etContraseña: EditText
    private lateinit var btnLogin: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etContraseña = findViewById(R.id.etContraseña)
        btnLogin = findViewById(R.id.btnLogin)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnLogin.setOnClickListener { iniciarSesionUsuario() }
    }

    private fun iniciarSesionUsuario() {
        val email = etEmail.text.toString()
        val contraseña = etContraseña.text.toString()

        mAuth.signInWithEmailAndPassword(email, contraseña)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    mAuth.currentUser?.let { user ->
                        val userId = user.uid

                        db.collection("users").document(userId).get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful && task.result.exists()) {
                                    val document = task.result
                                    val userData = document.data
                                    val username = userData?.get("username") as? String
                                    val rol = userData?.get("rol") as? String

                                    Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show()

                                    val intent = if (rol == "admin") {
                                        Intent(this@LoginActivity, CalendarioProfesor::class.java)
                                    } else {
                                        Intent(this@LoginActivity, CalendarioPadres::class.java)
                                    }

                                    intent.putExtra("username", username)
                                    intent.putExtra("userId", userId)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginActivity, "Error al obtener los datos del usuario.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Error en el inicio de sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}