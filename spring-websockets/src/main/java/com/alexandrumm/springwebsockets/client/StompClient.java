package com.alexandrumm.springwebsockets.client;

import com.alexandrumm.springwebsockets.controller.SimpleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class StompClient {

  private static final Logger log = LoggerFactory.getLogger(StompClient.class);
  private static final String STOMP_TIMEOUT = "stomp.timeout";
  private static final String SERVER_WS_URL = "ws://localhost:8085/spring-tutorials";
  private static final String SERVER_TUTORIAL_REQUEST_QUEUE = "/app/tutorial-request";

  private volatile WebSocketStompClient webSocketStompClient;
  private volatile StompSession stompSession;
  private volatile StompSessionHandler stompSessionHandler;
  private volatile ThreadPoolTaskScheduler taskScheduler;

  private volatile boolean isConnected;
  private volatile boolean connecting;

  @PostConstruct
  private void init() {
    taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.afterPropertiesSet();

    StandardWebSocketClient transport = new StandardWebSocketClient();
    webSocketStompClient = new WebSocketStompClient(transport);
    webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    webSocketStompClient.setTaskScheduler(taskScheduler);
    stompSessionHandler = new StompSessionHandler();
//    connect();
  }

  @PreDestroy
  public void destroy() {
    disconnect();
    taskScheduler.shutdown();
    taskScheduler.destroy();
  }

  public void disconnect() {
    try {
      if (stompSession != null && stompSession.isConnected()) {
        stompSession.disconnect();
      }
      if (webSocketStompClient != null) {
        webSocketStompClient.stop();
      }
    } catch (Exception e) {
      log.error("Error while disconnecting", e);
    }
    stompSession = null;
    isConnected = false;
  }

  private void connect() {
    connecting = true;
    stompSession = null;

    StompHeaders headers = new StompHeaders();
    WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
    isConnected = false;

    /* Connecting with callback */
    ListenableFuture<StompSession> connectFuture = webSocketStompClient
        .connect(SERVER_WS_URL, httpHeaders, headers, stompSessionHandler);
    try {
      int timeout = 20;
      String timeoutProp = System.getProperty(STOMP_TIMEOUT);
      if (timeoutProp != null) {
        timeout = Integer.parseInt(timeoutProp);
      }
      /* Waiting for the connection */
      stompSession = connectFuture.get(timeout, TimeUnit.SECONDS);
      if (stompSession != null && stompSession.isConnected()) {
        isConnected = true;
        log.info("STOMP is successfully connected!");
        sendMessage(SERVER_TUTORIAL_REQUEST_QUEUE, new SimpleRequest("Client", "Hello Server"));
      }
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("STOMP connection timeout: {}", e.getMessage(), e);
      disconnect();
    }
    connecting = false;
  }

  @Scheduled(cron = "* 0/2 * ? * *") /* At each 2 minutes starting from 0 */
  public void reconnect() throws InterruptedException {
    /* Check connection after losing session */
    if (stompSession != null && !stompSession.isConnected()) {
      log.error("STOMP session is disconnected (id: {})", stompSession.getSessionId());
      disconnect();
      return;
    }
    /* Trying to acquire new session */
    if (stompSession == null || !stompSession.isConnected()) {
      log.info("Trying to connect!");
      if (!connecting) {
        disconnect();
        /* Delaying the connection to a random time to reduce the swarming effect */
        int randomSleep = ThreadLocalRandom.current().nextInt(0, 30000);
        log.info("Connection is delayed with {} milliseconds", randomSleep);
        Thread.sleep(randomSleep);
        try {
          connect();
        } catch (Exception e) {
          log.error("Error while renewing the connection", e);
          stompSession = null;
          connecting = false;
          isConnected = false;
        }
      } else {
        log.info("Ongoing connection detected!");
      }
    }
  }

  public boolean isTransportConnected() {
    return stompSession != null && stompSession.isConnected();
  }

  public synchronized void sendMessage(String destination, SimpleRequest request) {
    if (!isConnected) {
      log.error("Cannot send message, while waiting to reconnect: {}", request);
    }
    stompSession.send(destination, request);
  }
}