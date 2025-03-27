package com.alizzelol.adaeasociacion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import java.util.*

class ConversationActivity : AppCompatActivity() {
    private lateinit var tvContactName: TextView
    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button
    private lateinit var adapter: MessageAdapter
    private val messages: MutableList<Mensaje> = mutableListOf()
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var username: String
    private lateinit var contactUsername: String
    private lateinit var conversationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        tvContactName = findViewById(R.id.tvContactName)
        rvMessages = findViewById(R.id.rvMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        intent?.let {
            username = it.getStringExtra("username") ?: return@let
            contactUsername = it.getStringExtra("contactUsername") ?: return@let
        } ?: run {
            Log.e("ConversationActivity", "Error: Intent nulo.")
            finish()
            return
        }

        conversationId = generateConversationId(username, contactUsername)
        tvContactName.text = contactUsername

        adapter = MessageAdapter(messages, username)
        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = adapter

        loadMessages()

        btnSend.setOnClickListener {
            val messageText = etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                etMessage.setText("")
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(RESULT_OK)
                val intent = Intent(this@ConversationActivity, ChatActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })
    }

    private fun generateConversationId(username1: String, username2: String): String {
        return listOf(username1, username2).sorted().joinToString("_")
    }

    private fun loadMessages() {
        db.collection("chats").document(conversationId).collection("mensajes")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ConversationActivity", "Error al cargar mensajes: ${e.message}")
                    return@addSnapshotListener
                }
                snapshots?.documentChanges?.forEach { dc ->
                    val message = dc.document.toObject(Mensaje::class.java)
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            messages.add(message)
                            adapter.notifyItemInserted(messages.size - 1)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            // Manejo de modificaciones si es necesario
                        }
                        DocumentChange.Type.REMOVED -> {
                            messages.remove(message)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }

    private fun sendMessage(messageText: String) {
        val conversationData = hashMapOf(
            "users" to listOf(username, contactUsername),
            "lastMessage" to messageText,
            "lastMessageTimestamp" to Date()
        )

        db.collection("chats").document(conversationId)
            .set(conversationData, SetOptions.merge())
            .addOnSuccessListener {
                val message = Mensaje(username, messageText, Date())
                db.collection("chats").document(conversationId).collection("mensajes")
                    .add(message)
                    .addOnFailureListener { e ->
                        Log.e("ConversationActivity", "Error al enviar mensaje: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("ConversationActivity", "Error al actualizar conversación: ${e.message}")
            }
    }
}
