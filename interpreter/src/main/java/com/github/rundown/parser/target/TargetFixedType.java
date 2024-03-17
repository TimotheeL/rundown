package com.github.rundown.parser.target;

import static java.lang.String.format;

import com.github.rundown.lexer.TokenType;

public enum TargetFixedType {
  HM,
  LT1,
  LT2,
  M,
  TEMPO,
  VO2_MAX;

  public static TargetFixedType fromTokenType(TokenType type) {
    return switch (type) {
      case HM -> HM;
      case LT1 -> LT1;
      case LT2 -> LT2;
      case M -> M;
      case TEMPO -> TEMPO;
      case VO2_MAX -> VO2_MAX;
      default -> throw new IllegalArgumentException(format("Token type %s is not a target fixed type", type));
    };
  }
}
