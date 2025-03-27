package com.alizzelol.adaeasociacion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class ContactListActivity : AppCompatActivity() {
    private var rvContacts: RecyclerView? = null
    private var adapter: ContactListAdapter? = null
    private var contacts: MutableList<User>? = null
    private var db: FirebaseFirestore? = null
    private var username: String? = null
    private var conversationLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        rvContacts = findViewById(R.id.rvContacts)
        username = intent.getStringExtra("username")

        Log.d("ContactListActivity", "Username: $username")

        contacts = ArrayList()
        adapter = ContactListAdapter(contacts ?: mutableListOf(), username ?: "", this)
        rvContacts?.layoutManager = LinearLayoutManager(this)
        rvContacts?.adapter = adapter

        db = FirebaseFirestore.getInstance()

        conversationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? -> }

        loadContacts()
    }

    private fun loadContacts() {
        db?.collection("users")?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                contacts?.clear()
                task.result?.forEach { document ->
                    val user = document.toObject(User::class.java)
                    if (user.username != username) {
                        contacts?.add(user)
                    }
                }
                adapter?.notifyDataSetChanged()
            } else {
                Log.e("ContactListActivity", "Error al cargar los contactos: ${task.exception}")
                Toast.makeText(this, "Error al cargar los contactos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun startConversationActivity(contactUsername: String) {
        val conversationId = generateConversationId(username, contactUsername)
        val users = listOfNotNull(username, contactUsername)
        val deletedBy: List<String>? = emptyList()

        Log.d("ContactListActivity", "Starting conversation with: $contactUsername")
        Log.d("ContactListActivity", "Generated ConversationId: $conversationId")

        db?.collection("chats")?.document(conversationId)?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                val intent = Intent(this, ConversationActivity::class.java).apply {
                    putExtra("username", username)
                    putExtra("contactUsername", contactUsername)
                    putExtra("conversationId", conversationId)
                }

                if (documentSnapshot.exists()) {
                    Log.d("ContactListActivity", "Conversation already exists.")
                    conversationLauncher?.launch(intent)
                } else {
                    Log.d("ContactListActivity", "Creating new conversation.")
                    val conversation = Conversation(
                        conversationId,
                        contactUsername,
                        "",
                        Date(),
                        users,
                        deletedBy
                    )

                    db?.collection("chats")?.document(conversationId)?.set(conversation)
                        ?.addOnSuccessListener {
                            Log.d("ContactListActivity", "Conversation created successfully.")
                            conversationLauncher?.launch(intent)
                        }
                        ?.addOnFailureListener { e ->
                            Log.e("ContactListActivity", "Error al crear la conversación: ${e.message}")
                            Toast.makeText(this, "Error al crear la conversación.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            ?.addOnFailureListener { e ->
                Log.e("ContactListActivity", "Error al verificar la conversación: ${e.message}")
                Toast.makeText(this, "Error al verificar la conversación.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateConversationId(username1: String?, username2: String): String {
        val usernames = arrayOf(username1 ?: "", username2)
        usernames.sort()
        val generatedId = "${usernames[0]}_${usernames[1]}"
        Log.d("ContactListActivity", "Generated ConversationId: $generatedId")
        return generatedId
    }
}
