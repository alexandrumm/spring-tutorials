package com.alexandrumm.springwebsockets.controller;

import java.io.Serializable;

public class SimpleResponse implements Serializable {

  private String text;

  public SimpleResponse() {
  }

  public SimpleResponse(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "SimpleResponse{" +
        "text='" + text + '\'' +
        '}';
  }
}
