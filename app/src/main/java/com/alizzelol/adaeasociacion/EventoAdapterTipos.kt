package com.alizzelol.adaeasociacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class EventoAdapterTipos(private val eventos: ArrayList<Evento>) :
    RecyclerView.Adapter<EventoAdapterTipos.EventoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_evento_tipos, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]
        holder.tituloTextView.text = evento.titulo
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.fechaTextView.text = sdf.format(evento.fecha)
        holder.descripcionTextView.text = evento.descripcion
        holder.horaTextView.text = evento.hora
    }

    override fun getItemCount(): Int {
        return eventos.size
    }

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tituloTextView: TextView = itemView.findViewById(R.id.tituloTextView)
        var fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
        var descripcionTextView: TextView =
            itemView.findViewById(R.id.descripcionTextView)
        var horaTextView: TextView = itemView.findViewById(R.id.horaTextView)
    }
}

