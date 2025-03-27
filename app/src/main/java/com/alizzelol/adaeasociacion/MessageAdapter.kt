package com.alizzelol.adaeasociacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class MessageAdapter(private val messages: List<Mensaje>, private val username: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.chat_item_right, parent, false)
            return SentMessageHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.chat_item_left, parent, false)
            return ReceivedMessageHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]

        return if (message.emisor == username) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    private class SentMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: TextView = itemView.findViewById(R.id.show_messageR)
        var timeText: TextView = itemView.findViewById(R.id.time_messageR)

        fun bind(message: Mensaje) {
            messageText.text = message.texto // Usar getTexto()
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = sdf.format(message.timestamp)
            timeText.text = time
        }
    }

    private class ReceivedMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: TextView = itemView.findViewById(R.id.show_messageL)
        var timeText: TextView = itemView.findViewById(R.id.time_messageL)

        fun bind(message: Mensaje) {
            messageText.text = message.texto // Usar getTexto()
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = sdf.format(message.timestamp)
            timeText.text = time
        }
    }

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }
}
