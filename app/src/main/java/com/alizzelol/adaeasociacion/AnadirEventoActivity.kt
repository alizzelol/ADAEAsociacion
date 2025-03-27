package com.alizzelol.adaeasociacion

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AnadirEventoActivity : AppCompatActivity() {

    private lateinit var editTextTitulo: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var editTextHora: EditText
    private lateinit var editTextTipoEvento: EditText
    private lateinit var buttonFecha: Button
    private lateinit var buttonGuardar: Button
    private lateinit var calendar: Calendar
    private lateinit var db: FirebaseFirestore
    private var fechaSeleccionada: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anadir_evento)

        editTextTitulo = findViewById(R.id.editTextTitulo)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)
        editTextHora = findViewById(R.id.editTextHora)
        editTextTipoEvento = findViewById(R.id.editTextTipoEvento)
        buttonFecha = findViewById(R.id.buttonFecha)
        buttonGuardar = findViewById(R.id.buttonGuardar)

        calendar = Calendar.getInstance()
        db = FirebaseFirestore.getInstance()

        buttonFecha.setOnClickListener { mostrarDatePicker() }
        buttonGuardar.setOnClickListener { guardarEvento() }
    }

    private fun mostrarDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                fechaSeleccionada = calendar.time
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                buttonFecha.text = sdf.format(fechaSeleccionada!!)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun guardarEvento() {
        val titulo = editTextTitulo.text.toString()
        val descripcion = editTextDescripcion.text.toString()
        val hora = editTextHora.text.toString()
        val tipo = editTextTipoEvento.text.toString().lowercase(Locale.getDefault())

        if (titulo.isEmpty() || descripcion.isEmpty() || hora.isEmpty() || fechaSeleccionada == null || tipo.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        try {
            sdf.parse(hora)
        } catch (e: ParseException) {
            Toast.makeText(this, "Formato de hora incorrecto (HH:mm).", Toast.LENGTH_SHORT).show()
            return
        }

        val evento = hashMapOf(
            "título" to titulo,
            "descripción" to descripcion,
            "fecha" to fechaSeleccionada!!,
            "hora" to hora,
            "tipo" to tipo
        )

        db.collection("eventos").add(evento)
            .addOnSuccessListener { _: DocumentReference? ->
                Toast.makeText(this, "Evento guardado con éxito.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { _: Exception? ->
                Toast.makeText(this, "Error al guardar el evento.", Toast.LENGTH_SHORT).show()
            }
    }
}