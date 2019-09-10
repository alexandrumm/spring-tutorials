package com.alexandrumm.springwebsockets.client;

import com.alexandrumm.springwebsockets.controller.SimpleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

public class TutorialRequestStompHandler implements StompFrameHandler {

  private static final Logger log = LoggerFactory.getLogger(TutorialRequestStompHandler.class);

  @Override
  public Type getPayloadType(StompHeaders headers) {
    return SimpleResponse.class;
  }

  @Override
  public void handleFrame(StompHeaders headers, Object payload) {
    log.info("Received: {}", payload);
    if (payload instanceof SimpleResponse) {
      SimpleResponse response = (SimpleResponse) payload;
      log.info("Received response {}", response.getText());
    }
  }
}
