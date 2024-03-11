package com.github.rundown.parser.target;

import static java.lang.String.format;

import com.github.rundown.lexer.TokenType;

public enum TargetFixedType {
  HMP,
  LT1,
  LT2,
  MP,
  TEMPO,
  VO2_MAX;

  public static TargetFixedType fromTokenType(TokenType type) {
    return switch (type) {
      case HMP -> HMP;
      case LT1 -> LT1;
      case LT2 -> LT2;
      case TEMPO -> TEMPO;
      case VO2_MAX -> VO2_MAX;
      default -> throw new IllegalArgumentException(format("Token type %s is not a target fixed type", type));
    };
  }
}
