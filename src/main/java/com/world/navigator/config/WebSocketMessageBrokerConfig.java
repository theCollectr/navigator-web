package com.world.navigator.config;

import com.world.navigator.security.AuthChannelInterceptorAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

  private final AuthChannelInterceptorAdapter authChannelInterceptorAdapter;

  public WebSocketMessageBrokerConfig(AuthChannelInterceptorAdapter authChannelInterceptorAdapter) {
    this.authChannelInterceptorAdapter = authChannelInterceptorAdapter;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic", "/queue");
    config.setApplicationDestinationPrefixes("/app");
    config.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/broadcast").withSockJS().setHeartbeatTime(60_000);
    registry.addEndpoint("/request").withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(authChannelInterceptorAdapter);
  }
}
