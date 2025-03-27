package com.alizzelol.adaeasociacion

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarioProfesor : AppCompatActivity() {

    private lateinit var calendarGrid: GridView
    private lateinit var calendar: Calendar
    private lateinit var days: MutableList<Date>
    private lateinit var events: MutableList<Evento>
    private lateinit var calendarAdapter: CalendarAdapterPro
    private lateinit var db: FirebaseFirestore
    private val filtro = "todos" // Filtro inicial
    private lateinit var textMesAño: TextView // Adicionado
    private var username: String? = null
    private lateinit var addEventLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_profesor)

        calendarGrid = findViewById(R.id.calendarGrid)
        calendar = Calendar.getInstance()
        db = FirebaseFirestore.getInstance()
        textMesAño = findViewById(R.id.textMesAno)
        val buttonPrevMonth = findViewById<ImageButton>(R.id.buttonPrevMonth)
        val buttonNextMonth = findViewById<ImageButton>(R.id.buttonNextMonth)
        val btnChat = findViewById<Button>(R.id.btnChat)

        username = intent.getStringExtra("username")

        buttonPrevMonth.setOnClickListener { mostrarMesAnterior() }
        buttonNextMonth.setOnClickListener { mostrarMesSiguiente() }

        generateCalendar()
        loadEvents()
        updateCalendar()
        actualizarTextoMesAño()

        addEventLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                loadEvents()
                updateCalendar()
            }
        }

        calendarGrid.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedDate = days[position]
            mostrarEventosDelDia(selectedDate)
        }

        btnChat.setOnClickListener {
            val intent = Intent(this@CalendarioProfesor, ChatActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }

    private fun mostrarMesAnterior() {
        calendar.add(Calendar.MONTH, -1)
        generateCalendar()
        loadEvents()
        updateCalendar()
        actualizarTextoMesAño()
    }

    private fun mostrarMesSiguiente() {
        calendar.add(Calendar.MONTH, 1)
        generateCalendar()
        loadEvents()
        updateCalendar()
        actualizarTextoMesAño()
    }

    private fun actualizarTextoMesAño() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        val mesAño = sdf.format(calendar.time)
        textMesAño.text = mesAño.uppercase(Locale.getDefault())
    }

    private fun generateCalendar() {
        days = mutableListOf()
        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
        tempCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)
        for (i in 0..41) {
            days.add(tempCalendar.time)
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun loadEvents() {
        events = mutableListOf()
        db.collection("eventos")
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    events.clear()
                    for (document in task.result) {
                        val evento = Evento(
                            document.id,
                            document.getString("título") ?: "", // Manejo de nulos
                            document.getString("descripción") ?: "", // Manejo de nulos
                            document.getDate("fecha"),
                            document.getString("hora") ?: "", // Manejo de nulos
                            document.getString("tipo") ?: "" // Manejo de nulos
                        )
                        events.add(evento)
                    }
                    updateCalendar()
                } else {
                    Toast.makeText(this, "Error al cargar eventos.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateCalendar() {
        calendarAdapter = CalendarAdapterPro(this, days, events, calendar)
        calendarGrid.adapter = calendarAdapter
    }

    private fun mostrarEventosDelDia(dataSelecionada: Date) {
        val eventosDoDia = mutableListOf<Evento>()
        val calDataSelecionada = Calendar.getInstance()
        calDataSelecionada.time = dataSelecionada

        events.forEach { evento ->
            val calEvento = Calendar.getInstance()
            calEvento.time = evento.fecha

            if (calDataSelecionada.get(Calendar.YEAR) == calEvento.get(Calendar.YEAR) &&
                calDataSelecionada.get(Calendar.MONTH) == calEvento.get(Calendar.MONTH) &&
                calDataSelecionada.get(Calendar.DAY_OF_MONTH) == calEvento.get(Calendar.DAY_OF_MONTH)
            ) {
                eventosDoDia.add(evento)
            }
        }

        if (eventosDoDia.isNotEmpty()) {
            val intent = Intent(this, EventosDiaActivity::class.java)
            intent.putParcelableArrayListExtra("eventos", ArrayList(eventosDoDia))
            startActivity(intent)
        } else {
            Toast.makeText(this, "No hay eventos este día.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_calendario, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_anadir_usuario -> {
                startActivity(Intent(this, AnadirUsuarioActivity::class.java))
                true
            }
            R.id.action_lista_usuarios -> {
                startActivity(Intent(this, ListaUsuariosActivity::class.java))
                true
            }
            R.id.action_anadir_evento -> {
                addEventLauncher.launch(Intent(this, AnadirEventoActivity::class.java))
                true
            }
            R.id.action_lista_eventos -> {
                startActivity(Intent(this, ListaEventosActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

