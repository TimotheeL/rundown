package com.github.rundown.parser.distance;

import static com.github.rundown.lexer.TokenType.FLOAT;
import static com.github.rundown.lexer.TokenType.NUMBER;
import static com.github.rundown.lexer.TokenType.WHITE_SPACE;

import com.github.rundown.lexer.Token;
import com.github.rundown.parser.Expression.Distance;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.TokenGroups;

public class DistanceParser {
  private final Parser parser;

  public DistanceParser(Parser parser) {
    this.parser = parser;
  }

  public Distance distance() {
    Distance distanceUnqualified = distanceUnqualified();
    if (distanceUnqualified != null) {
      return distanceUnqualified;
    }

    return distanceQualified();
  }

  public Distance distanceUnqualified() {
    if (parser.match(NUMBER, FLOAT)) {
      Token distance = parser.previous();
      if (parser.match(TokenGroups.DISTANCE_UNITS)) {
        return new Distance(Double.parseDouble(distance.value()), DistanceUnit.fromTokenType(parser.previous().type()));
      }
      parser.setCurrent(parser.getCurrent() - 1);
    }
    return null;
  }

  public Distance distanceQualified() {
    int current = parser.getCurrent();
    if (parser.match(NUMBER, FLOAT)) {
      Token distance = parser.previous();
      if (parser.match(WHITE_SPACE) && parser.match(TokenGroups.DISTANCE_UNITS_QUALIFIED)) {
        return new Distance(Double.parseDouble(distance.value()), DistanceUnit.fromTokenType(parser.previous().type()));
      }
    }
    parser.setCurrent(current);
    return null;
  }
}
