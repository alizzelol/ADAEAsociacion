package com.alizzelol.adaeasociacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class ListaEventosAdapter(
    private val listaEventos: List<Evento>,
    private val listener: OnListaEventoClickListener
) :
    RecyclerView.Adapter<ListaEventosAdapter.ListaEventosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaEventosViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_evento_lista, parent, false)
        return ListaEventosViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListaEventosViewHolder, position: Int) {
        val evento = listaEventos[position]
        holder.textViewTitulo.text = evento.titulo
        holder.textViewDescripcion.text = evento.descripcion
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.textViewFecha.text = sdf.format(evento.fecha)
        holder.textViewHora.text = evento.hora
        holder.textViewTipo.text = evento.tipo
        holder.itemView.setOnClickListener { listener.onListaEventoClick(evento.id) }
    }

    override fun getItemCount(): Int {
        return listaEventos.size
    }

    class ListaEventosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        var textViewDescripcion: TextView = itemView.findViewById(R.id.textViewDescripcion)
        var textViewFecha: TextView = itemView.findViewById(R.id.textViewFecha)
        var textViewHora: TextView = itemView.findViewById(R.id.textViewHora)
        var textViewTipo: TextView = itemView.findViewById(R.id.textViewTipo)
    }
}

