package com.alizzelol.adaeasociacion

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ListaPadresInscritos : AppCompatActivity() {
    private var recyclerViewPadres: RecyclerView? = null
    private var padresInscritosAdapter: PadresInscritosAdapter? = null
    private val listaPadres: MutableList<String> = ArrayList()
    private var db: FirebaseFirestore? = null
    private var mensajeNoPadres: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_padres_inscritos)

        val recyclerViewPadresLocal = findViewById<RecyclerView>(R.id.recyclerViewPadres)
        recyclerViewPadresLocal.layoutManager = LinearLayoutManager(this)

        val mensajeNoPadresLocal = findViewById<TextView>(R.id.mensajeNoPadres)

        db = FirebaseFirestore.getInstance()
        cargarPadresInscritos(recyclerViewPadresLocal, mensajeNoPadresLocal)
    }

    private fun cargarPadresInscritos(recyclerViewPadres: RecyclerView, mensajeNoPadres: TextView) {
        val eventoId = intent.getStringExtra("eventoId")
        db!!.collection("inscripciones")
            .whereEqualTo("eventoId", eventoId)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    listaPadres.clear()
                    if (task.result.isEmpty) {
                        // No hay inscripciones, mostrar mensaje
                        mensajeNoPadres.visibility = View.VISIBLE
                        recyclerViewPadres.visibility = View.GONE
                    } else {
                        // Hay inscripciones, cargar datos
                        mensajeNoPadres.visibility = View.GONE
                        recyclerViewPadres.visibility = View.VISIBLE

                        for (document in task.result) {
                            val userId = document.getString("padreId")
                            db!!.collection("users").document(userId!!)
                                .get()
                                .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                        val nombrePadre =
                                            documentSnapshot.getString("nombre") + " " + documentSnapshot.getString(
                                                "apellido"
                                            )
                                        listaPadres.add(nombrePadre)
                                        padresInscritosAdapter = PadresInscritosAdapter(listaPadres)
                                        recyclerViewPadres.adapter = padresInscritosAdapter
                                    }
                                }
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Error al cargar padres inscritos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}