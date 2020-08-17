package com.example.mytripsapplication.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Message implements Comparable<Message>{
    private String content,senderId, senderName,type= "TEXT",groupId;
    private ArrayList<String> receiverIds;
    private Date time;
    private boolean deleted;

    public Message() {
    }

    public Message(String content) {
        this.content = content;
        this.time = new Date();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getReceiverIds() {
        return receiverIds;
    }

    public void setReceiverIds(ArrayList<String> receiverIds) {
        this.receiverIds = receiverIds;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public HashMap toHashMap(){
        HashMap<String, Object> messageMap = new HashMap();
        messageMap.put("content",this.content);
        messageMap.put("senderId",this.senderId);
        messageMap.put("senderName",this.senderName);
        messageMap.put("groupId",this.groupId);
        messageMap.put("receiverIds",this.receiverIds);
        messageMap.put("time",this.time);
        messageMap.put("type",this.type);
        messageMap.put("deleted",this.deleted);
        return  messageMap;
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", senderId='" + senderId + '\'' +
                ", senderName='" + senderName + '\'' +
                ", type='" + type + '\'' +
                ", groupId='" + groupId + '\'' +
                ", receiverIds=" + receiverIds +
                ", time=" + time +
                ", deleted=" + deleted +
                '}';
    }

    @Override
    public int compareTo(Message o) {
        return this.getTime().compareTo(o.getTime());
    }
}
