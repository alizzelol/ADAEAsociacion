package com.alizzelol.adaeasociacion

class User {
    var username: String? = null
    var nombre: String? = null
    var apellido: String? = null
    var email: String? = null
    var telefono: String? = null
    var rol: String? = null
    var userId: String? = null // o documentId, como prefieras
    var documentId: String? = null

    constructor()

    constructor(
        username: String?,
        nombre: String?,
        apellido: String?,
        email: String?,
        telefono: String?,
        rol: String?,
        userId: String?,
        documentId: String?
    ) {
        this.username = username
        this.nombre = nombre
        this.apellido = apellido
        this.email = email
        this.telefono = telefono
        this.rol = rol
        this.userId = userId
        this.documentId = documentId
    }
}