package com.example.mytripsapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.Section

class ChatRoom : AppCompatActivity() {


    private lateinit var currentChannelId: String
    private lateinit var currentUser: User
    private lateinit var receiverIds: ArrayList<String>
    private lateinit var tripName: String

    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
