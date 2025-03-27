package com.alizzelol.adaeasociacion

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PadresInscritosAdapter(private val listaPadres: List<String>) :
    RecyclerView.Adapter<PadresInscritosAdapter.PadreViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PadreViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.simple_list_item_1, parent, false)
        return PadreViewHolder(view)
    }

    override fun onBindViewHolder(holder: PadreViewHolder, position: Int) {
        val padre = listaPadres[position]
        holder.textViewNombrePadre.text = padre
    }

    override fun getItemCount(): Int {
        return listaPadres.size
    }

    class PadreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewNombrePadre: TextView = itemView.findViewById(R.id.text1)
    }
}

