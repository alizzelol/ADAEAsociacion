package com.alizzelol.adaeasociacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class EventoAdapterApuntarse(
    private val eventos: List<Evento>,
    private val listener: OnEventoClickListener
) : RecyclerView.Adapter<EventoAdapterApuntarse.EventoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evento_lista, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]
        holder.textViewTitulo.text = evento.titulo
        holder.textViewDescripcion.text = evento.descripcion
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.textViewFecha.text = sdf.format(evento.fecha)
        holder.textViewTipo.text = evento.tipo
        holder.textViewHora.text = evento.hora
        holder.itemView.setOnClickListener { listener.onEventoClick(evento.id) }
    }

    override fun getItemCount(): Int {
        return eventos.size
    }

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        val textViewDescripcion: TextView = itemView.findViewById(R.id.textViewDescripcion)
        val textViewFecha: TextView = itemView.findViewById(R.id.textViewFecha)
        val textViewHora: TextView = itemView.findViewById(R.id.textViewHora)
        val textViewTipo: TextView = itemView.findViewById(R.id.textViewTipo)
    }

    interface OnEventoClickListener {
        fun onEventoClick(eventoId: String?)
    }
}