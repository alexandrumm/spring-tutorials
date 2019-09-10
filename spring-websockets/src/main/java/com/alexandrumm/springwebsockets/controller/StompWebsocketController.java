package com.alexandrumm.springwebsockets.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class StompWebsocketController {

  private static final Logger log = LoggerFactory.getLogger(StompWebsocketController.class);

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/tutorial-request")
  public void handleRequest(SimpleRequest request, SimpMessageHeaderAccessor headerAccessor) {
    String sessionId = headerAccessor.getSessionId();
    log.info("Handling session with id {} receiving message {} from {}", sessionId, request.getText(), request.getFrom());
    SimpleResponse response = new SimpleResponse("Hello!");
    messagingTemplate.convertAndSendToUser(headerAccessor.getUser().getName(), "/queue/tutorial-request", response);
    log.info("Replied to {} with message {}", sessionId, response);
  }
}
