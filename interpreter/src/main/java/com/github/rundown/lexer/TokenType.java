package com.github.rundown.lexer;

public enum TokenType {
  AT("@"),
  BPM("bpm"),
  COLON(":"),
  COMMA(","),
  CYCLE("C"),
  COOLDOWN("cooldown|CD"),
  DOWNHILL("downhill"),
  EASY("easy"),
  EQUAL("="),
  EOF(null),
  FLOAT("\\d+\\.\\d+"),
  GAP("gap"),
  HM("HM"),
  HARD("hard"),
  HILLY("hilly"),
  HOUR("h"),
  KILOMETER("km|k"),
  KILOMETER_QUALIFIED("Kilometer"),
  LT1("LT1"),
  LT2("LT2"),
  M("M"), // Can either represent Mile or Marathon, depending on context
  METER("m"),
  METER_QUALIFIED("Meter"),
  MILE_QUALIFIED("Mile"),
  MINUS("\\-"),
  MINUTE("mn|'"),
  MULTIPLIER("x"),
  NUMBER("\\d+"),
  PARENTHESIS_LEFT("\\("),
  PARENTHESIS_RIGHT("\\)"),
  PROGRESSION_REP(">"),
  PROGRESSION_SET(">>"),
  RACE_PACE("P"),
  RECOVERY_JOG("R"),
  RECOVERY_STATIC("S"),
  RECOVERY_WALK("W"),
  RPE("rpe"),
  SECOND("s|\""),
  SEMICOLON(";"),
  SLASH("/"),
  SPM("spm"),
  STEADY("steady"),
  STRIDES("strides"),
  TEMPO("tempo"),
  THRESHOLD("threshold"),
  TRACK("track"),
  TREADMILL("treadmill"),
  UPHILL("uphill"),
  VO2_MAX("VO2max"),
  WATTS("W"),
  WHITE_SPACE("\\s+"),
  WARM_UP("warmup|WU"),
  YARD("yd"),
  YARD_QUALIFIED("Yard"),
  ZONE("Z");

  private final String regexPattern;

  TokenType(String regexPattern) {
    this.regexPattern = regexPattern;
  }

  public String regexPattern() {
    return regexPattern;
  }
}
