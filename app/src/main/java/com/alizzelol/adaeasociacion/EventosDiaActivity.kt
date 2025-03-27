package com.alizzelol.adaeasociacion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EventosDiaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_dia)

        val recyclerViewEventosDia = findViewById<RecyclerView>(R.id.recyclerViewEventosDia)
        recyclerViewEventosDia.layoutManager = LinearLayoutManager(this)

        val eventos = intent.getSerializableExtra("eventos") as ArrayList<Evento>?
        val adapter = EventosDiaAdapter(eventos!!)
        recyclerViewEventosDia.adapter = adapter
    }
}