package com.github.rundown.parser.distance;

import static java.lang.String.format;

import com.github.rundown.lexer.TokenType;

public enum DistanceUnit {
  KILOMETER,
  METER,
  MILE,
  YARD;

  public static DistanceUnit fromTokenType(TokenType type) {
    return switch (type) {
      case KILOMETER, KILOMETER_QUALIFIED -> KILOMETER;
      case METER, METER_QUALIFIED -> METER;
      case M, MILE_QUALIFIED -> MILE;
      case YARD, YARD_QUALIFIED -> YARD;
      default -> throw new IllegalArgumentException(format("Token type %s is not a distance unit", type));
    };
  }

  @Override
  public String toString() {
    return switch (this) {
      case KILOMETER -> "km";
      case METER -> "m";
      case MILE -> "M";
      case YARD -> "yd";
    };
  }
}
