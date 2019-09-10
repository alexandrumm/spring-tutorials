package com.alexandrumm.springwebsockets.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public class StompSessionHandler extends StompSessionHandlerAdapter {

  private static final Logger log = LoggerFactory.getLogger(StompSessionHandler.class);

  @Override
  public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    log.info("Connected!");
    /* Add here new subscriptions with its own handler */
    session.subscribe("/user/queue/tutorial-request", new TutorialRequestStompHandler());
  }

  @Override
  public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
    log.info("Received headers: {}", headers.toString());
    log.error(exception.getMessage(), exception);
  }
}
