package me.yh.config;

import me.yh.socket.ChatHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;

    public WebSocketConfig(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 해당 endpoint로 handshake가 이루어진다.
        registry.addHandler(chatHandler, "/ws/chat")
                .setAllowedOrigins("/")//크로스 도메인 허용
                //allowedOriginPatterns으로 대체될 예정
                // handshake의 after, before 정보를 HandshakeInterceptor를 통해 가로 챌 수 있다.
                // spring에서 제공하는 HttpSessionHandshakeInterceptor 을 다음과 같이 넣어준다.
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .withSockJS();//SockJS 허용
    }

    @Bean //메시지 버퍼크기
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        return container;
    }
}