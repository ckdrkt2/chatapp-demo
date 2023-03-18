package com.chat.chatapp.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry

import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws").withSockJS()
    }

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // 메시지 핸들러로 라우팅되는 prefix("/app")을 파라미터로 지정
        config.setApplicationDestinationPrefixes("/app")
        // 내장 메시지 브로커를 사용하기 위한 메소드
        // 파라미터로 지정한 prefix("/topic" 또는 "/queue")가 붙은 메시지일 경우 브로커가 이를 처리
        config.enableSimpleBroker("/topic", "/queue")
        config.setUserDestinationPrefix("/users")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(UserInterceptor())
    }
}

// STOMP를 사용하면, 웹소켓만 사용할 때와 다르게 하나의 연결주소마다 핸들러 클래스를 따로 구현할 필요없이 Controller 방식으로 간편하게 사용할 수 있음