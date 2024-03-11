package com.github.rundown.parser.time;

import static java.lang.String.format;

import com.github.rundown.lexer.TokenType;

public enum TimeUnit {
  HOUR,
  MINUTE,
  SECOND;

  public static TimeUnit fromTokenType(TokenType type) {
    return switch (type) {
      case HOUR -> HOUR;
      case MINUTE -> MINUTE;
      case SECOND -> SECOND;
      default -> throw new IllegalArgumentException(format("Token type %s is not a time unit", type));
    };
  }

  @Override
  public String toString() {
    return switch (this) {
      case HOUR -> "h";
      case MINUTE -> "mn";
      case SECOND -> "s";
    };
  }
}
