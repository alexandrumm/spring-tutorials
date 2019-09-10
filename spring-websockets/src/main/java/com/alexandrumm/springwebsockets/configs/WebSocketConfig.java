package com.alexandrumm.springwebsockets.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends WebSocketMessageBrokerConfigurationSupport implements WebSocketMessageBrokerConfigurer {

  @Autowired
  private CustomHandshakeHandler customHandshakeHandler;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/spring-tutorials")  /* Actual relative URL where to connect */
        /* In order to set an id for the session since we make use of anonymous users */
        .setHandshakeHandler(customHandshakeHandler)
        .setAllowedOrigins("*");
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.setApplicationDestinationPrefixes("/app");
    config.enableSimpleBroker("/topic", "/queue"); /* Channels */
    config.setPreservePublishOrder(true);
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    super.configureWebSocketTransport(registry);
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
  }

  @Override
  public void configureClientOutboundChannel(ChannelRegistration registration) {
    super.configureClientOutboundChannel(registration);
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    super.addArgumentResolvers(argumentResolvers);
  }

  @Override
  public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
    super.addReturnValueHandlers(returnValueHandlers);
  }

  @Override
  public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
    messageConverters.add(new MappingJackson2MessageConverter());
    return true;
  }
}
