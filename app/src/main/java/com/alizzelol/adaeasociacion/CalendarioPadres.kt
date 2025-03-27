package com.alizzelol.adaeasociacion

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarioPadres : AppCompatActivity() {
    private var calendarGrid: GridView? = null
    private var calendar: Calendar? = null
    private var days: MutableList<Date>? = null
    private var events: MutableList<Evento>? = null
    private var calendarAdapter: CalendarAdapterPadres? = null
    private var db: FirebaseFirestore? = null
    private var textMesAño: TextView? = null
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_padres)

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

        calendarGrid?.setOnItemClickListener { _, _, position, _ ->
            days?.get(position)?.let { selectedDate ->
                mostrarEventosDelDia(selectedDate)
            }
        }

        btnChat.setOnClickListener {
            val intent = Intent(this@CalendarioPadres, ChatActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }

    private fun mostrarMesAnterior() {
        calendar?.add(Calendar.MONTH, -1)
        generateCalendar()
        loadEvents()
        updateCalendar()
        actualizarTextoMesAño()
    }

    private fun mostrarMesSiguiente() {
        calendar?.add(Calendar.MONTH, 1)
        generateCalendar()
        loadEvents()
        updateCalendar()
        actualizarTextoMesAño()
    }

    private fun actualizarTextoMesAño() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        val mesAño = calendar?.time?.let { sdf.format(it) } ?: ""
        textMesAño?.text = mesAño.uppercase(Locale.getDefault())
    }

    private fun generateCalendar() {
        days = ArrayList()
        val tempCalendar = calendar?.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
        tempCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)
        for (i in 0..41) {
            days?.add(tempCalendar.time)
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun loadEvents() {
        events = ArrayList()
        db?.collection("eventos")
            ?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    events?.clear()
                    for (document in task.result) {
                        val evento = Evento(
                            document.id,
                            document.getString("título") ?: "",
                            document.getString("descripcion") ?: "",
                            document.getDate("fecha"),
                            document.getString("hora") ?: "",
                            document.getString("tipo") ?: ""
                        )
                        events?.add(evento)
                    }
                    updateCalendar()
                } else {
                    Toast.makeText(this, "Error al cargar eventos.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateCalendar() {
        days?.let { daysList ->
            events?.let { eventsList ->
                calendar?.let { calendarInstance ->
                    calendarAdapter = CalendarAdapterPadres(this, daysList, eventsList, calendarInstance)
                    calendarGrid?.adapter = calendarAdapter
                }
            }
        }
    }

    private fun mostrarEventosDelDia(dataSelecionada: Date) {
        val eventosDoDia: MutableList<Evento> = ArrayList()
        val calDataSelecionada = Calendar.getInstance()
        calDataSelecionada.time = dataSelecionada

        events?.forEach { evento ->
            evento.fecha?.let { fechaEvento ->
                val calEvento = Calendar.getInstance()
                calEvento.time = fechaEvento

                if (calDataSelecionada.get(Calendar.YEAR) == calEvento.get(Calendar.YEAR) &&
                    calDataSelecionada.get(Calendar.MONTH) == calEvento.get(Calendar.MONTH) &&
                    calDataSelecionada.get(Calendar.DAY_OF_MONTH) == calEvento.get(Calendar.DAY_OF_MONTH)
                ) {
                    eventosDoDia.add(evento)
                }
            }
        }

        if (eventosDoDia.isNotEmpty()) {
            val intent = Intent(this, EventosDiaPadresActivity::class.java)
            intent.putExtra("eventos", eventosDoDia as ArrayList<Evento>)
            startActivity(intent)
        } else {
            Toast.makeText(this, "No hay eventos este día.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_calendario_padres, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_ver_cursos -> mostrarListaEventosPorTipo("curso")
            R.id.action_ver_talleres -> mostrarListaEventosPorTipo("taller")
            R.id.action_apuntarse_evento -> startActivity(Intent(this, ApuntarseEventoActivity::class.java))
            R.id.action_mis_eventos -> startActivity(Intent(this, MisEventosActivity::class.java))
            R.id.action_perfil -> {
                FirebaseAuth.getInstance().currentUser?.let { currentUser ->
                    val intent = Intent(this, PerfilUsuarioActivity::class.java)
                    intent.putExtra("userId", currentUser.uid)
                    startActivity(intent)
                } ?: run {
                    Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun mostrarListaEventosPorTipo(tipo: String) {
        val eventosFiltrados: MutableList<Evento> = ArrayList()
        events?.forEach { evento ->
            if (evento.tipo.equals(tipo, ignoreCase = true)) {
                eventosFiltrados.add(evento)
            }
        }

        eventosFiltrados.sortBy { it.fecha }

        val intent = Intent(this, ListaEventosPorTipos::class.java)
        intent.putExtra("eventos", eventosFiltrados as ArrayList<Evento>)
        intent.putExtra("tipoEvento", tipo)
        startActivity(intent)
    }
}