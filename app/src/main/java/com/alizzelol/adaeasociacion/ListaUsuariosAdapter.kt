package com.alizzelol.adaeasociacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListaUsuariosAdapter(
    private val listaUsuarios: List<User>,
    private val listener: OnUserClickListener
) : RecyclerView.Adapter<ListaUsuariosAdapter.UserViewHolder>() {

    interface OnUserClickListener {
        fun onUserClick(userId: String?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        holder.textViewNombre.text = usuario.nombre
        holder.textViewApellido.text = usuario.apellido
        holder.textViewEmail.text = usuario.email
        holder.textViewTelefono.text = usuario.telefono
        holder.textViewRol.text = usuario.rol
        holder.itemView.setOnClickListener { listener.onUserClick(usuario.userId) }
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var textViewNombre: TextView
        lateinit var textViewApellido: TextView
        lateinit var textViewEmail: TextView
        lateinit var textViewTelefono: TextView
        lateinit var textViewRol: TextView

        init {
            textViewNombre = itemView.findViewById(R.id.textViewNombre)
            textViewApellido = itemView.findViewById(R.id.textViewApellido)
            textViewEmail = itemView.findViewById(R.id.textViewEmail)
            textViewTelefono = itemView.findViewById(R.id.textViewTelefono)
            textViewRol = itemView.findViewById(R.id.textViewRol)
        }
    }
}