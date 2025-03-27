package com.alizzelol.adaeasociacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class EditarUsuarioActivity : AppCompatActivity() {
    private var editTextNombre: EditText? = null
    private var editTextApellido: EditText? = null
    private var editTextEmail: EditText? = null
    private var editTextTelefono: EditText? = null
    private var editTextRol: EditText? = null
    private var buttonGuardar: Button? = null
    private var db: FirebaseFirestore? = null
    private var userId: String? = null
    private var documentSnapshotUsuario: DocumentSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuario)

        val editTextNombreLocal = findViewById<EditText>(R.id.editTextNombre)
        val editTextApellidoLocal = findViewById<EditText>(R.id.editTextApellido)
        val editTextEmailLocal = findViewById<EditText>(R.id.editTextEmail)
        val editTextTelefonoLocal = findViewById<EditText>(R.id.editTextTelefono)
        val editTextRolLocal = findViewById<EditText>(R.id.editTextRol)
        val buttonGuardarLocal = findViewById<Button>(R.id.buttonGuardar)

        db = FirebaseFirestore.getInstance()
        userId = intent.getStringExtra("userId")

        cargarDatosUsuario(
            editTextNombreLocal,
            editTextApellidoLocal,
            editTextEmailLocal,
            editTextTelefonoLocal,
            editTextRolLocal
        )

        buttonGuardarLocal.setOnClickListener { guardarCambios(editTextNombreLocal, editTextApellidoLocal, editTextEmailLocal, editTextTelefonoLocal, editTextRolLocal) }
    }

    private fun cargarDatosUsuario(
        editTextNombre: EditText,
        editTextApellido: EditText,
        editTextEmail: EditText,
        editTextTelefono: EditText,
        editTextRol: EditText
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
                    editTextRol.setText(documentSnapshot.getString("rol"))
                } else {
                    Toast.makeText(this, "Usuario no encontrado.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Error al cargar datos del usuario.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
    }

    private fun guardarCambios(
        editTextNombre: EditText,
        editTextApellido: EditText,
        editTextEmail: EditText,
        editTextTelefono: EditText,
        editTextRol: EditText
    ) {
        if (documentSnapshotUsuario == null) {
            Toast.makeText(this, "Error: No se cargaron los datos del usuario.", Toast.LENGTH_SHORT).show()
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
        if (editTextRol.text.toString() != documentSnapshotUsuario!!.getString("rol")) {
            usuarioActualizado["rol"] = editTextRol.text.toString()
        }

        if (usuarioActualizado.isNotEmpty()) {
            db!!.collection("users").document(userId!!)
                .update(usuarioActualizado)
                .addOnSuccessListener {
                    Toast.makeText(this, "Usuario actualizado con éxito.", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar usuario.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se realizaron cambios.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}