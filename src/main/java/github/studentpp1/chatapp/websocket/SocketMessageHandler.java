package github.studentpp1.chatapp.websocket;

import github.studentpp1.chatapp.redis.Publisher;
import github.studentpp1.chatapp.redis.Subscriber;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SocketMessageHandler extends TextWebSocketHandler {
    private final WebSocketSessionManager sessionManager;
    private final Publisher redisPublisher;
    private final Subscriber redisSubscriber;

    public SocketMessageHandler(WebSocketSessionManager sessionManager, Publisher redisPublisher, Subscriber redisSubscriber) {
        this.sessionManager = sessionManager;
        this.redisPublisher = redisPublisher;
        this.redisSubscriber = redisSubscriber;
    }

    @Override
    public void afterConnectionEstablished(
            WebSocketSession session
    ) throws Exception {
        // create a session
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ) throws Exception {
        // delete session
        super.afterConnectionClosed(session, status);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // message: <text_object> from:<groupName / userName>, message: ".."
        //  create a Chatroom (generate name as <name>&<timestamp>
        System.out.println(message.getPayload()); // parse as json
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "John");
        jsonObject.put("age", 30);
        jsonObject.put("married", true);
        String jsonString = jsonObject.toString();
        // ws.send(JSON.stringify({"some":"some"}))
        System.out.println(jsonString);
        // this.redisPublisher.publish("<groupName / userName>", jsonString);
    }
}
