package com.alizzelol.adaeasociacion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class ChatActivity : AppCompatActivity(), ConversationAdapter.OnItemClickListener {
    private var rvConversations: RecyclerView? = null
    private var fabNewChat: FloatingActionButton? = null
    private var username: String? = null
    private var adapter: ConversationAdapter? = null
    private var conversations: MutableList<Conversation> = ArrayList()
    private var db: FirebaseFirestore? = null
    private var mAuth: FirebaseAuth? = null
    private var conversationLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val rvConversationsLocal = findViewById<RecyclerView>(R.id.rvConversations)
        val fabNewChatLocal = findViewById<FloatingActionButton>(R.id.fabNewChat)
        username = intent.getStringExtra("username")

        adapter = ConversationAdapter(conversations, username ?: "", this)
        rvConversationsLocal.layoutManager = LinearLayoutManager(this)
        rvConversationsLocal.adapter = adapter

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        conversationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                loadConversations()
            }
        }

        fabNewChatLocal.setOnClickListener {
            val intent = Intent(this@ChatActivity, ContactListActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        rvConversations = rvConversationsLocal
        fabNewChat = fabNewChatLocal

        loadConversations()
        setupRecyclerViewListener()
    }

    private fun loadConversations() {
        db?.collection("chats")
            ?.whereArrayContains("users", username ?: return)
            ?.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ChatActivity", "Error al escuchar las conversaciones: ${e.message}")
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    conversations.clear()
                    for (document in snapshots) {
                        val conversation = document.toObject(Conversation::class.java)
                        if (conversation != null && conversation.deletedBy.orEmpty().contains(username).not()) {
                            conversations.add(conversation)
                        }
                    }
                    adapter?.notifyDataSetChanged()
                }
            }
    }

    override fun onItemClick(conversation: Conversation?) {
        if (conversation == null) return

        val intent = Intent(this@ChatActivity, ConversationActivity::class.java)
        intent.putExtra("username", username)

        val contactUsername = conversation.users?.firstOrNull { it != username } ?: ""
        intent.putExtra("contactUsername", contactUsername)
        intent.putExtra("conversationId", conversation.conversationId)
        conversationLauncher?.launch(intent)
    }


    private fun setupRecyclerViewListener() {
        rvConversations?.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                val btnDelete = view.findViewById<ImageButton>(R.id.btnDeleteConversation)
                btnDelete?.setOnClickListener {
                    val conversationId = btnDelete.tag as? String
                    conversationId?.let { deleteConversation(it) }
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {}
        })
    }

    private fun deleteConversation(conversationId: String) {
        db?.collection("chats")?.document(conversationId)?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                val conversation = documentSnapshot.toObject(Conversation::class.java)
                if (conversation != null) {
                    val deletedBy = conversation.deletedBy.orEmpty().toMutableList()
                    if (!deletedBy.contains(username)) {
                        deletedBy.add(username!!)
                    }

                    db?.collection("chats")?.document(conversationId)
                        ?.update("deletedBy", deletedBy)
                        ?.addOnSuccessListener {
                            Log.d("ChatActivity", "Conversación marcada como eliminada para el usuario.")
                            loadConversations()
                        }
                        ?.addOnFailureListener { e ->
                            Log.e("ChatActivity", "Error al marcar conversación como eliminada: ${e.message}")
                        }
                }
            }
            ?.addOnFailureListener { e ->
                Log.e("ChatActivity", "Error al obtener conversación: ${e.message}")
            }
    }
}
