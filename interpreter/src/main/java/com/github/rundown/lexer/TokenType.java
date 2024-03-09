package com.github.rundown.lexer;

public enum TokenType {
  COLON(":"),
  COMMA(","),
  EQUAL("="),
  EOF(null),
  FLOAT("\\d+\\.\\d+"),
  KEYWORD("cooldown|CD|downhill|easy|hard|hilly|"
      + "steady|strides|tempo|threshold|track|uphillwarmup|WU"),
  HOUR("h"),
  KILOMETER("km|k"),
  KILOMETER_QUALIFIED("Kilometer"),
  METER("m"),
  METER_QUALIFIED("Meter"),
  MILE("M"),
  MILE_QUALIFIED("Mile"),
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
  YARD("yd"),
  YARD_QUALIFIED("Yard");

  private final String regexPattern;

  TokenType(String regexPattern) {
    this.regexPattern = regexPattern;
  }

  public String regexPattern() {
    return regexPattern;
  }
}
