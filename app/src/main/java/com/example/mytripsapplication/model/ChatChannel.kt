package com.example.mytripsapplication.model

class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}