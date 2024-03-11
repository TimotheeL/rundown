package com.github.rundown.parser.recovery;

import static java.lang.String.format;

import com.github.rundown.lexer.TokenType;

public enum RecoveryType {
  JOG,
  WALK,
  STATIC;

  public static RecoveryType fromTokenType(TokenType type) {
    return switch (type) {
      case RECOVERY_JOG -> JOG;
      case RECOVERY_WALK -> WALK;
      case RECOVERY_STATIC -> STATIC;
      default -> throw new IllegalArgumentException(format("Token type %s is not a recovery type", type));
    };
  }
}
