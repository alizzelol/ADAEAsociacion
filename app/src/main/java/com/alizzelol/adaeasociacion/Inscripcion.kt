package com.alizzelol.adaeasociacion

class Inscripcion {
    var padreId: String? = null
    var eventoId: String? = null

    // Constructor vacío (necesario para Firestore)
    constructor()

    constructor(padreId: String?, eventoId: String?) {
        this.padreId = padreId
        this.eventoId = eventoId
    }

    override fun toString(): String {
        return "Inscripcion{" +
                "padreId='" + padreId + '\'' +
                ", eventoId='" + eventoId + '\'' +
                '}'
    }
}
