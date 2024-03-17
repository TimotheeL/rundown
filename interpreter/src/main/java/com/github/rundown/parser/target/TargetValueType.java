package com.github.rundown.parser.target;

import static java.lang.String.format;

import com.github.rundown.lexer.TokenType;

public enum TargetValueType {
  FIXED,
  GAP,
  HEARTRATE,
  PACE,
  RACE_PACE,
  POWER,
  RPE,
  SPEED,
  STEPS,
  TIME,
  ZONE;

  public static TargetValueType fromTokenType(TokenType type) {
    return switch (type) {
      case BPM -> HEARTRATE;
      case GAP -> GAP;
      case RACE_PACE -> RACE_PACE;
      case RPE -> RPE;
      case SPM -> STEPS;
      case WATTS -> POWER;
      case ZONE -> ZONE;
      default -> throw new IllegalArgumentException(format("Token type %s is not a target value type", type));
    };
  }
}
