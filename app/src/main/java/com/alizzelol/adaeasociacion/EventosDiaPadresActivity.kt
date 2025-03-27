package com.alizzelol.adaeasociacion

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EventosDiaPadresActivity : AppCompatActivity() {
    private var eventosRecyclerView: RecyclerView? = null
    private var eventoAdapter: EventoAdapterPadres? = null
    private var eventosDelDia: List<Evento>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_dia_padres)

        val eventosRecyclerViewLocal = findViewById<RecyclerView>(R.id.eventosRecyclerView)
        eventosRecyclerViewLocal.layoutManager = LinearLayoutManager(this)

        // Obtener la lista de eventos del Intent usando la versión no obsoleta
        val eventosDelDiaLocal = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("eventos", ArrayList::class.java) as? ArrayList<Evento>
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("eventos") as? ArrayList<Evento>
        }

        if (eventosDelDiaLocal != null) {
            // Inicializar el adaptador y configurar el RecyclerView
            val eventoAdapterLocal = EventoAdapterPadres(eventosDelDiaLocal)
            eventosRecyclerViewLocal.adapter = eventoAdapterLocal
        }
    }
}
