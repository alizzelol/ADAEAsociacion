package com.alizzelol.adaeasociacion

import java.util.Date

class Mensaje {
    @JvmField
    var emisor: String? = null
    @JvmField
    var texto: String? = null
    @JvmField
    var timestamp: Date? = null

    constructor()

    constructor(emisor: String?, texto: String?, timestamp: Date?) {
        this.emisor = emisor
        this.texto = texto
        this.timestamp = timestamp
    }
}
