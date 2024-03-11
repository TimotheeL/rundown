package com.github.rundown.parser.target;

import static java.lang.String.format;

import com.github.rundown.lexer.TokenType;

public enum TargetRangeType {
  RANGE,
  PROGRESSION_REP,
  PROGRESSION_SET;

  public static TargetRangeType fromTokenType(TokenType type) {
    return switch (type) {
      case MINUS -> RANGE;
      case PROGRESSION_REP -> PROGRESSION_REP;
      case PROGRESSION_SET -> PROGRESSION_SET;
      default -> throw new IllegalArgumentException(format("Token type %s is not a target range type", type));
    };
  }
}
