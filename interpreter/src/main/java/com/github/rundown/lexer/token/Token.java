package com.github.rundown.lexer.token;

public record Token(TokenType type, String value, int startPosition) {

  @Override
  public String toString() {
    return String.format("Token(%s, %s, %d)", type, value, startPosition);
  }
}
