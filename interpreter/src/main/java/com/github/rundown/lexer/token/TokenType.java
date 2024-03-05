package com.github.rundown.lexer.token;

public enum TokenType {
  COLON(":"),
  COMMA(","),
  EQUAL("="),
  EOF(null),
  HOUR("h"),
  KILOMETER("km|k"),
  METER("m"),
  MILE("M"),
  MINUTE("mn|'"),
  MULTIPLIER("x"),
  NUMBER("\\d+"),
  PARENTHESIS_LEFT("\\("),
  PARENTHESIS_RIGHT("\\)"),
  RECOVERY_JOG("R"),
  RECOVERY_STATIC("S"),
  RECOVERY_WALK("W"),
  SECOND("s|\""),
  SEMICOLON(";"),
  WHITE_SPACE("\\s+"),
  YARD("yd");

  private final String regexPattern;

  TokenType(String regexPattern) {
    this.regexPattern = regexPattern;
  }

  public String regexPattern() {
    return regexPattern;
  }
}
