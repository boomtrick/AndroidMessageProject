package com.example.mthompson.chattychitchat;

public class ChatMessage {
    public  String chatName;
    public String messageText;
    public String username;
    public long messageTime;

    public ChatMessage(){}

    public ChatMessage(String chatName,String messageText, String username,long messageTime )
    {
        this.messageText = messageText;
        this.username = username;
        this.messageTime = messageTime;
        this.chatName = chatName;
    }
}
