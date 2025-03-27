package com.alizzelol.adaeasociacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventosDiaAdapter(private val eventos: List<Evento>) :
    RecyclerView.Adapter<EventosDiaAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_evento_dia, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = eventos[position]
        holder.textViewTitulo.text = evento.titulo
        holder.textViewHora.text = evento.hora
        holder.textViewTipo.text = evento.tipo
    }

    override fun getItemCount(): Int {
        return eventos.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        var textViewHora: TextView = itemView.findViewById(R.id.textViewHora)
        var textViewTipo: TextView = itemView.findViewById(R.id.textViewTipo)
    }
}
