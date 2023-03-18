package com.chat.chatapp.config

import com.chat.chatapp.model.User
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor

class UserInterceptor: ChannelInterceptor {
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (accessor != null && StompCommand.CONNECT == accessor.command) {
            println(1)
            val raw = message.headers[SimpMessageHeaderAccessor.NATIVE_HEADERS]
            if (raw is Map<*, *>) {
                val name = raw["username"]
                if (name is ArrayList<*>) {
                    accessor.user = User((name as ArrayList<String>)[0])
                }
            }
        }
        return message
    }
}

