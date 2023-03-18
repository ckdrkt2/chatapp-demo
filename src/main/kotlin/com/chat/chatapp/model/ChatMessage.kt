package com.chat.chatapp.model

data class ChatMessage (
    var type: MessageType,
    var content: String?,
    var sender: String,
    var receiver: String,
)

enum class MessageType {
    CHAT,
    JOIN,
    LEAVE
}