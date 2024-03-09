package com.github.rundown.parser.time;

public class TimeFormatException extends RuntimeException {

  public TimeFormatException(String message) {
    super("Invalid time format: " + message);
  }
}
