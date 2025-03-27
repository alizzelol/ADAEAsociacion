package com.alizzelol.adaeasociacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class EventoAdapter(
    private val listaEventos: List<Evento>,
    private val listener: OnEventoClickListener
) :
    RecyclerView.Adapter<EventoAdapter.EventoViewHolder>() {
    interface OnEventoClickListener {
        fun onEventoClick(eventoId: String?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_evento_lista, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = listaEventos[position]
        holder.textViewTitulo.text = evento.titulo
        holder.textViewDescripcion.text = evento.descripcion
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.textViewFecha.text = sdf.format(evento.fecha)
        holder.textViewHora.text = evento.hora
        holder.textViewTipo.text = evento.tipo
        holder.itemView.setOnClickListener { v: View? -> listener.onEventoClick(evento.id) }
    }

    override fun getItemCount(): Int {
        return listaEventos.size
    }

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        var textViewDescripcion: TextView =
            itemView.findViewById(R.id.textViewDescripcion)
        var textViewFecha: TextView = itemView.findViewById(R.id.textViewFecha)
        var textViewHora: TextView = itemView.findViewById(R.id.textViewHora)
        var textViewTipo: TextView = itemView.findViewById(R.id.textViewTipo)
    }
}
