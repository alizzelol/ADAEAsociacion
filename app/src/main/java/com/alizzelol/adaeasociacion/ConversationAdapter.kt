package com.alizzelol.adaeasociacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat

class ConversationAdapter(
    private val conversations: MutableList<Conversation>,
    private val currentUsername: String,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(conversation: Conversation?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.conversation_item, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]

        val contactName = conversation.users?.find { it != currentUsername } ?: "Desconocido"
        holder.tvContactName.text = contactName

        holder.tvLastMessage.text = conversation.lastMessage

        holder.tvLastTimestamp.text = conversation.lastTimestamp?.let {
            DateFormat.getDateTimeInstance().format(it)
        } ?: ""

        holder.btnDeleteConversation.tag = conversation.conversationId ?: ""

        holder.itemView.setOnClickListener {
            listener.onItemClick(conversation)
        }
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContactName: TextView = itemView.findViewById(R.id.tvContactName)
        val tvLastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
        val tvLastTimestamp: TextView = itemView.findViewById(R.id.tvLastTimestamp)
        val btnDeleteConversation: ImageButton = itemView.findViewById(R.id.btnDeleteConversation)
    }
}
