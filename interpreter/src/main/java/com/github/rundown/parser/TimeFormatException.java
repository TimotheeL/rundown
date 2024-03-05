package com.github.rundown.parser;

public class TimeFormatException extends RuntimeException {

  public TimeFormatException(String message) {
    super("Invalid time format: " + message);
  }
}
