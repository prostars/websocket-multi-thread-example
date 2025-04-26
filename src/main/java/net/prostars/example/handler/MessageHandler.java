package net.prostars.example.handler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MessageHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
  protected final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    sessions.put(session.getId(), session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
      throws IOException {
    WebSocketSession webSocketSession = sessions.remove(session.getId());
    if (webSocketSession != null) {
      webSocketSession.close();
    }
  }

  @Override
  protected void handleTextMessage(WebSocketSession senderSession, TextMessage message) {
    try {
      for (WebSocketSession session : sessions.values()) {
        if (!senderSession.getId().equals(session.getId())) {
          session.sendMessage(new TextMessage(message.getPayload()));
          log.info("Send {} to {}", message.getPayload(), session.getId());
        }
      }
    } catch (Exception ex) {
      log.error("Failed to send from {} error: {}", senderSession.getId(), ex.getMessage());
    }
  }
}
