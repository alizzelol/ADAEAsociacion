package com.alizzelol.adaeasociacion

import java.util.Date

class Conversation {
    @JvmField
    var conversationId: String? = null
    @JvmField
    var lastMessage: String? = null
    var lastSender: String? = null
    @JvmField
    var lastTimestamp: Date? = null
    @JvmField
    var users: List<String>? = null
    var deletedBy: List<String>? = null

    constructor()

    constructor(
        conversationId: String?,
        lastSender: String?,
        lastMessage: String?,
        lastTimestamp: Date?,
        users: List<String>?,
        deletedBy: List<String>?
    ) {
        this.conversationId = conversationId
        this.lastSender = lastSender
        this.lastMessage = lastMessage
        this.lastTimestamp = lastTimestamp
        this.users = users
        this.deletedBy = deletedBy
    }
}

