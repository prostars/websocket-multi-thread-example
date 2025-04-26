package net.prostars.example.config;

import net.prostars.example.handler.ConcurrentMessageHandler;
import net.prostars.example.handler.MessageHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@SuppressWarnings("unused")
public class WebSocketHandlerConfig implements WebSocketConfigurer {

  private final MessageHandler messageHandler;
  private final ConcurrentMessageHandler concurrentMessageHandler;

  public WebSocketHandlerConfig(
      MessageHandler messageHandler, ConcurrentMessageHandler concurrentMessageHandler) {
    this.messageHandler = messageHandler;
    this.concurrentMessageHandler = concurrentMessageHandler;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(messageHandler, "/ws/v1/message")
        .addHandler(concurrentMessageHandler, "/ws/v2/message");
  }
}
