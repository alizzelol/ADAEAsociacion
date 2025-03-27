package com.alizzelol.adaeasociacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventoAdapterMisEventos(
    private val eventos: List<Evento>,
    private val listener: OnEventoClickListener
) : RecyclerView.Adapter<EventoAdapterMisEventos.EventoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evento_mis_eventos, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]
        holder.tituloTextView.text = evento.titulo
        holder.descripcionTextView.text = evento.descripcion
        holder.horaTextView.text = evento.hora
        holder.tipoTextView.text = evento.tipo

        holder.itemView.setOnClickListener { listener.onEventoClick(evento) }
    }

    override fun getItemCount(): Int {
        return eventos.size
    }

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloTextView: TextView = itemView.findViewById(R.id.textViewTitulo)
        val descripcionTextView: TextView = itemView.findViewById(R.id.textViewDescripcion)
        val horaTextView: TextView = itemView.findViewById(R.id.textViewHora)
        val tipoTextView: TextView = itemView.findViewById(R.id.textViewTipo)
    }

    interface OnEventoClickListener {
        fun onEventoClick(evento: Evento) // Cambiado a Evento (no nullable)
    }
}