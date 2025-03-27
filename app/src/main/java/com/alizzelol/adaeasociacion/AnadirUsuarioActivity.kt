package com.alizzelol.adaeasociacion

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class AnadirUsuarioActivity : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextPassword2: EditText
    private lateinit var editTextTelefono: EditText
    private lateinit var editTextRol: EditText
    private lateinit var buttonAñadir: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anadir_usuario)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellido = findViewById(R.id.editTextApellido)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextPassword2 = findViewById(R.id.editTextPassword2)
        editTextTelefono = findViewById(R.id.editTextTelefono)
        editTextRol = findViewById(R.id.editTextRol)
        buttonAñadir = findViewById(R.id.buttonAñadir)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        buttonAñadir.setOnClickListener { añadirUsuario() }
    }

    private fun añadirUsuario() {
        val nombre = editTextNombre.text.toString()
        val apellido = editTextApellido.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        val password2 = editTextPassword2.text.toString()
        val telefono = editTextTelefono.text.toString()
        val rol = editTextRol.text.toString().lowercase(Locale.getDefault())
        val username = "$nombre $apellido"

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty() || telefono.isEmpty() || rol.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != password2) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    mAuth.currentUser?.let { user ->
                        val userId = user.uid
                        val userData = hashMapOf(
                            "username" to username.lowercase(Locale.getDefault()),
                            "nombre" to nombre,
                            "apellido" to apellido,
                            "email" to email,
                            "userId" to userId,
                            "rol" to rol,
                            "telefono" to telefono
                        )

                        db.collection("users").document(userId).set(userData)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this@AnadirUsuarioActivity, "Usuario añadido con éxito.", Toast.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    Toast.makeText(this@AnadirUsuarioActivity, "Error al guardar los datos del usuario.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(this@AnadirUsuarioActivity, "Error al crear usuario.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}