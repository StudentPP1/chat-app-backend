package github.studentpp1.chatapp.websocket;

import lombok.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class ConectionInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
           @NonNull ServerHttpRequest request,
           @NonNull ServerHttpResponse response,
           @NonNull WebSocketHandler wsHandler,
           @NonNull Map<String, Object> attributes
    ) {
        // http://localhost:8080/user/userName
        //  1. create a user "mailBox"
        //  2. put user's userName in attributes to find a "mailBox"
        String path = request.getURI().getPath();
        String userName = path.substring(path.lastIndexOf('/') + 1);
        attributes.put("userName", userName);
        return true;
    }

    @Override
    public void afterHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            Exception exception
    ) {
        // pass
    }
}
