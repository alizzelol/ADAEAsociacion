package com.alizzelol.adaeasociacion

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class PerfilUsuarioActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var userId: String? = null
    private var documentSnapshotUsuario: DocumentSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        val editTextNombreLocal = findViewById<EditText>(R.id.editTextNombre)
        val editTextApellidoLocal = findViewById<EditText>(R.id.editTextApellido)
        val editTextEmailLocal = findViewById<EditText>(R.id.editTextEmail)
        val editTextTelefonoLocal = findViewById<EditText>(R.id.editTextTelefono)
        val editTextPasswordLocal = findViewById<EditText>(R.id.editTextPassword)
        val etConfirmPasswordLocal = findViewById<EditText>(R.id.etConfirmPassword)
        val buttonGuardarLocal = findViewById<Button>(R.id.buttonGuardar)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = mAuth?.currentUser
        if (currentUser != null) {
            Log.d("PerfilUsuarioActivity", "Usuario autenticado: " + currentUser.uid)
            userId = currentUser.uid
            cargarDatosUsuario(
                editTextNombreLocal,
                editTextApellidoLocal,
                editTextEmailLocal,
                editTextTelefonoLocal
            )
        } else {
            Log.d("PerfilUsuarioActivity", "Usuario no autenticado.")
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
            finish()
        }

        buttonGuardarLocal.setOnClickListener {
            guardarCambios(
                editTextNombreLocal,
                editTextApellidoLocal,
                editTextEmailLocal,
                editTextTelefonoLocal,
                editTextPasswordLocal,
                etConfirmPasswordLocal
            )
        }
    }

    private fun cargarDatosUsuario(
        editTextNombre: EditText,
        editTextApellido: EditText,
        editTextEmail: EditText,
        editTextTelefono: EditText
    ) {
        db!!.collection("users").document(userId!!)
            .get()
            .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                if (documentSnapshot.exists()) {
                    documentSnapshotUsuario = documentSnapshot
                    editTextNombre.setText(documentSnapshot.getString("nombre"))
                    editTextApellido.setText(documentSnapshot.getString("apellido"))
                    editTextEmail.setText(documentSnapshot.getString("email"))
                    editTextTelefono.setText(documentSnapshot.getString("telefono"))
                } else {
                    Toast.makeText(this, "Usuario no encontrado.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos del usuario.", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun guardarCambios(
        editTextNombre: EditText,
        editTextApellido: EditText,
        editTextEmail: EditText,
        editTextTelefono: EditText,
        editTextPassword: EditText,
        etConfirmPassword: EditText
    ) {
        if (documentSnapshotUsuario == null) {
            Toast.makeText(this, "Error: No se cargaron los datos del usuario.", Toast.LENGTH_SHORT).show()
            return
        }

        val password = editTextPassword.text.toString()
        val password2 = etConfirmPassword.text.toString()

        if (password.isNotEmpty() && password != password2) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return
        }

        val usuarioActualizado: MutableMap<String, Any> = HashMap()

        if (editTextNombre.text.toString() != documentSnapshotUsuario!!.getString("nombre")) {
            usuarioActualizado["nombre"] = editTextNombre.text.toString()
        }
        if (editTextApellido.text.toString() != documentSnapshotUsuario!!.getString("apellido")) {
            usuarioActualizado["apellido"] = editTextApellido.text.toString()
        }
        if (editTextEmail.text.toString() != documentSnapshotUsuario!!.getString("email")) {
            usuarioActualizado["email"] = editTextEmail.text.toString()
        }
        if (editTextTelefono.text.toString() != documentSnapshotUsuario!!.getString("telefono")) {
            usuarioActualizado["telefono"] = editTextTelefono.text.toString()
        }

        if (usuarioActualizado.isNotEmpty()) {
            db!!.collection("users").document(userId!!)
                .update(usuarioActualizado)
                .addOnSuccessListener {
                    Toast.makeText(this, "Usuario actualizado con éxito.", Toast.LENGTH_SHORT).show()
                    if (password.isNotEmpty()) {
                        actualizarContraseña(password)
                    } else {
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar usuario.", Toast.LENGTH_SHORT).show()
                }
        } else if (password.isNotEmpty()) {
            actualizarContraseña(password)
        } else {
            Toast.makeText(this, "No se realizaron cambios.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun actualizarContraseña(password: String) {
        val user = mAuth?.currentUser
        user?.updatePassword(password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Contraseña actualizada con éxito.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar contraseña.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}