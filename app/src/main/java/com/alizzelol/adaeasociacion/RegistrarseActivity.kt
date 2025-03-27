package com.alizzelol.adaeasociacion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class RegistrarseActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContraseña: EditText
    private lateinit var etRol: EditText
    private lateinit var btnRegistrarse: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

        etUsuario = findViewById(R.id.etUsuario)
        etEmail = findViewById(R.id.etEmail)
        etContraseña = findViewById(R.id.etContraseña)
        etRol = findViewById(R.id.etRol)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnRegistrarse.setOnClickListener {
            val usuario = etUsuario.text.toString().lowercase(Locale.getDefault())
            val email = etEmail.text.toString()
            val contraseña = etContraseña.text.toString()
            val rol = etRol.text.toString().lowercase(Locale.getDefault())

            mAuth.createUserWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener(this@RegistrarseActivity) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        user?.let {
                            val userId = it.uid
                            val userData = hashMapOf(
                                "username" to usuario,
                                "email" to email,
                                "userId" to userId,
                                "rol" to rol
                            )

                            db.collection("users").document(userId).set(userData)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this@RegistrarseActivity, "Registro exitoso.", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@RegistrarseActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this@RegistrarseActivity, "Error al guardar los datos del usuario.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this@RegistrarseActivity, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}