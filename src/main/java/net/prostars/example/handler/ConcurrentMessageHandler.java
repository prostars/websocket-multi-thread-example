package net.prostars.example.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

@Component
public class ConcurrentMessageHandler extends MessageHandler {

  private static final Logger log = LoggerFactory.getLogger(ConcurrentMessageHandler.class);

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    log.info("ConnectionEstablished: {}", session.getId());
    sessions.put(
        session.getId(), new ConcurrentWebSocketSessionDecorator(session, 5000, 100 * 1024));
  }
}
