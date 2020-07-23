package com.example.mytripsapplication.model

import java.util.*

data class ImageMessage (val imagePath: String,
                         override val time: Date,
                         override val senderId: String,
                         override val recipientIds: ArrayList<String>,
                         override val senderName: String,
                         override val type: String = MessageType.IMAGE)
    : Message {
    constructor() : this("", Date(0), "",  ArrayList<String>(), "")
}