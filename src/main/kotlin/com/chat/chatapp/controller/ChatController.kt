package com.chat.chatapp.controller

import com.chat.chatapp.model.ChatMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller


@Controller
class ChatController(private val messagingTemplate: SimpMessagingTemplate) {
    @MessageMapping("/chat.sendMessage")
    // WebSocketBrokerConfig에서 설정한 /app prefix와 합쳐진 /app/chat.sendMessage라는 목적지 헤더를 가진 메시지들이 이 핸들러를 거침
    @SendTo("/topic/public")
    // 결과 메시지를 설정한 경로 /topic/public으로 보내줌
    fun sendMessage(@Payload chatMessage: ChatMessage?): ChatMessage? {
        return chatMessage
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    fun addUser(@Payload chatMessage: ChatMessage, headerAccessor: SimpMessageHeaderAccessor): ChatMessage? {
        headerAccessor.sessionAttributes!!["username"] = chatMessage.sender
        return chatMessage
    }

    @MessageMapping("/hello")
    @SendToUser("/queue/messages")
    fun sendMessage(sha: SimpMessageHeaderAccessor, @Payload chatMessage: ChatMessage): ChatMessage {
        val receiver = chatMessage.receiver
        messagingTemplate.convertAndSendToUser(receiver, "/queue/messages", chatMessage)
        return chatMessage
    }
}