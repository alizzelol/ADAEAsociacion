package com.alizzelol.adaeasociacion

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Evento(
    val id: String, // String no nullable
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: Date?,
    val hora: String = "",
    val tipo: String = ""
) : Parcelable