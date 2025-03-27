package com.alizzelol.adaeasociacion

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarAdapterPadres(
    private val context: Context,
    private val days: List<Date>,
    private val events: List<Evento>,
    private val currentCalendar: Calendar
) :
    BaseAdapter() {
    override fun getCount(): Int {
        return days.size
    }

    override fun getItem(position: Int): Any {
        return days[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val linearLayout = if (convertView == null) {
            LayoutInflater.from(context)
                .inflate(R.layout.item_calendar_day, parent, false) as LinearLayout
        } else {
            convertView as LinearLayout
        }

        val dayOfMonthTextView = linearLayout.findViewById<TextView>(R.id.dayOfMonth)
        val eventsContainer = linearLayout.findViewById<LinearLayout>(R.id.eventsContainer)
        eventsContainer.removeAllViews() // Limpiar eventos anteriores

        val day = days[position]
        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = day
        val dayOfMonth = tempCalendar[Calendar.DAY_OF_MONTH]

        val sdf = SimpleDateFormat("d", Locale.getDefault())
        val dayString = sdf.format(day)
        dayOfMonthTextView.text = dayString

        if (isSameMonth(day, currentCalendar.time)) {
            for (evento in events) { // Cambio importante aquí
                if (isSameDay(day, evento.fecha!!)) { // Acceder a las propiedades directamente
                    val eventTextView = TextView(context)
                    eventTextView.text = evento.titulo
                    eventTextView.setTextColor(Color.BLACK)
                    eventTextView.textSize = 12f
                    eventTextView.setPadding(5, 2, 5, 2)

                    // Aplicar color de fondo según el tipo de evento
                    if (evento.tipo == "taller") {
                        eventTextView.setBackgroundColor(
                            Color.rgb(
                                100,
                                149,
                                237
                            )
                        ) // Color para talleres
                    } else if (evento.tipo == "curso") {
                        eventTextView.setBackgroundColor(
                            Color.rgb(
                                50,
                                205,
                                50
                            )
                        ) // Color para cursos
                    }

                    eventsContainer.addView(eventTextView)
                }
            }

            if (eventsContainer.childCount > 0) {
                linearLayout.setBackgroundColor(
                    Color.rgb(
                        220,
                        220,
                        220
                    )
                ) // Cambiar color si hay eventos
            } else {
                linearLayout.setBackgroundColor(Color.WHITE)
            }

            dayOfMonthTextView.setTextColor(Color.BLACK)
        } else {
            linearLayout.setBackgroundColor(Color.LTGRAY)
            dayOfMonthTextView.setTextColor(Color.GRAY)
        }

        return linearLayout
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] && cal1[Calendar.MONTH] == cal2[Calendar.MONTH] && cal1[Calendar.DAY_OF_MONTH] == cal2[Calendar.DAY_OF_MONTH]
    }

    private fun isSameMonth(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] &&
                cal1[Calendar.MONTH] == cal2[Calendar.MONTH]
    }
}