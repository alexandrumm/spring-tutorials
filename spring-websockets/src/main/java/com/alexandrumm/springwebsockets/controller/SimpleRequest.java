package com.alexandrumm.springwebsockets.controller;

import java.io.Serializable;

public class SimpleRequest implements Serializable {

  private String from;
  private String text;

  public SimpleRequest() {
  }

  public SimpleRequest(String from, String text) {
    this.from = from;
    this.text = text;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "SimpleRequest{" +
        "from='" + from + '\'' +
        ", text='" + text + '\'' +
        '}';
  }
}
