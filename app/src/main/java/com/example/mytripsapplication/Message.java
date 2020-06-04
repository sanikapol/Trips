package com.example.mytripsapplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Message {
    private static final AtomicInteger count = new AtomicInteger(0);
    private  String content;
    private int msgId,senderId;
    private ArrayList<Integer> receiverIds;
    Date dateCreated;

    public Message(String content, int senderId, Date dateCreated) {
        this.msgId = count.incrementAndGet();
        this.content = content;
        this.senderId = senderId;
        this.dateCreated = dateCreated;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return msgId;
    }

    public void setId(int id) {
        this.msgId = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", msgId=" + msgId +
                ", senderId=" + senderId +
                ", receiverIds=" + receiverIds +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
