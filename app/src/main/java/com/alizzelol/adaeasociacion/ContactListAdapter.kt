package com.alizzelol.adaeasociacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactListAdapter(
    private val contacts: List<User>,
    private val username: String,
    private val activity: ContactListActivity
) :
    RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.tvContactName.text = contact.username
        holder.itemView.setOnClickListener { activity.startConversationActivity(contact.username!!) }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvContactName: TextView = itemView.findViewById(R.id.tvContactName)
    }
}