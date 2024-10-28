package github.studentpp1.chatapp.websocket;

import github.studentpp1.chatapp.redis.Publisher;
import github.studentpp1.chatapp.redis.Subscriber;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketSessionManager sessionManager;
    private final Publisher redisPublisher;
    private final Subscriber redisSubscriber;
    private final ConectionInterceptor conectionInterceptor;

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(
                new SocketMessageHandler(
                        this.sessionManager,
                        this.redisPublisher,
                        this.redisSubscriber
                ),
                "/user/*" // create a user messageBox
        )
                .addInterceptors(conectionInterceptor)
                .setAllowedOrigins("*"); // allow all senders
    }
}
