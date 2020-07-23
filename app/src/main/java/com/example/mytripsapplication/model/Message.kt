package com.example.mytripsapplication.model

import java.util.*

object MessageType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Message {

    val time: Date
    val senderId: String
    val recipientIds: ArrayList<String>
    val senderName: String
    val type: String
}