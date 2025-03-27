package com.alizzelol.adaeasociacion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class DetallesUsuarioActivity : AppCompatActivity() {
    private var textViewNombre: TextView? = null
    private var textViewApellido: TextView? = null
    private var textViewEmail: TextView? = null
    private var textViewTelefono: TextView? = null
    private var textViewRol: TextView? = null
    private var buttonEliminar: Button? = null
    private var buttonEditar: Button? = null
    private var db: FirebaseFirestore? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_usuario)

        val textViewNombreLocal = findViewById<TextView>(R.id.textViewNombre)
        val textViewApellidoLocal = findViewById<TextView>(R.id.textViewApellido)
        val textViewEmailLocal = findViewById<TextView>(R.id.textViewEmail)
        val textViewTelefonoLocal = findViewById<TextView>(R.id.textViewTelefono)
        val textViewRolLocal = findViewById<TextView>(R.id.textViewRol)
        val buttonEliminarLocal = findViewById<Button>(R.id.buttonEliminar)
        val buttonEditarLocal = findViewById<Button>(R.id.buttonEditar)

        db = FirebaseFirestore.getInstance()
        userId = intent.getStringExtra("userId")

        cargarDetallesUsuario(textViewNombreLocal, textViewApellidoLocal, textViewEmailLocal, textViewTelefonoLocal, textViewRolLocal)

        buttonEliminarLocal.setOnClickListener { eliminarUsuario() }
        buttonEditarLocal.setOnClickListener { editarUsuario() }
    }

    private fun cargarDetallesUsuario(
        textViewNombre: TextView,
        textViewApellido: TextView,
        textViewEmail: TextView,
        textViewTelefono: TextView,
        textViewRol: TextView
    ) {
        db!!.collection("users").document(userId!!)
            .get()
            .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                if (documentSnapshot.exists()) {
                    textViewNombre.text = "Nombre: " + documentSnapshot.getString("nombre")
                    textViewApellido.text = "Apellido: " + documentSnapshot.getString("apellido")
                    textViewEmail.text = "Email: " + documentSnapshot.getString("email")
                    textViewTelefono.text = "Teléfono: " + documentSnapshot.getString("telefono")
                    textViewRol.text = "Rol: " + documentSnapshot.getString("rol")
                } else {
                    Toast.makeText(this, "Usuario no encontrado.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Error al cargar detalles del usuario.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
    }

    private fun eliminarUsuario() {
        db!!.collection("users").document(userId!!)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario eliminado con éxito.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar usuario.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editarUsuario() {
        val intent = Intent(
            this@DetallesUsuarioActivity,
            EditarUsuarioActivity::class.java
        )
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}