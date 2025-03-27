package com.alizzelol.adaeasociacion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ListaUsuariosActivity : AppCompatActivity() {

    private lateinit var recyclerViewUsuarios: RecyclerView
    private lateinit var listaUsuariosAdapter: ListaUsuariosAdapter
    private val listaUsuarios: MutableList<User> = mutableListOf()
    private lateinit var db: FirebaseFirestore
    private lateinit var editUserLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_usuarios)

        recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios)
        recyclerViewUsuarios.layoutManager = LinearLayoutManager(this)
        db = FirebaseFirestore.getInstance()

        editUserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                cargarUsuarios()
            }
        }

        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        db.collection("users")
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    listaUsuarios.clear()
                    for (document in task.result) {
                        val usuario = User(
                            document.getString("username") ?: "",
                            document.getString("nombre") ?: "",
                            document.getString("apellido") ?: "",
                            document.getString("email") ?: "",
                            document.getString("telefono") ?: "",
                            document.getString("rol") ?: "",
                            document.getString("userId") ?: "",
                            document.id
                        )
                        listaUsuarios.add(usuario)
                    }

                    listaUsuariosAdapter = ListaUsuariosAdapter(listaUsuarios, object : ListaUsuariosAdapter.OnUserClickListener {
                        override fun onUserClick(userId: String?) {
                            val intent = Intent(this@ListaUsuariosActivity, EditarUsuarioActivity::class.java)
                            intent.putExtra("userId", userId)
                            editUserLauncher.launch(intent)
                        }
                    })
                    recyclerViewUsuarios.adapter = listaUsuariosAdapter
                } else {
                    Toast.makeText(this, "Error al cargar usuarios.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}