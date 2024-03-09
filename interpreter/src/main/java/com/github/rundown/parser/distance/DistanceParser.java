package com.github.rundown.parser.distance;

import static com.github.rundown.lexer.TokenType.FLOAT;
import static com.github.rundown.lexer.TokenType.KILOMETER;
import static com.github.rundown.lexer.TokenType.KILOMETER_QUALIFIED;
import static com.github.rundown.lexer.TokenType.METER;
import static com.github.rundown.lexer.TokenType.METER_QUALIFIED;
import static com.github.rundown.lexer.TokenType.MILE;
import static com.github.rundown.lexer.TokenType.MILE_QUALIFIED;
import static com.github.rundown.lexer.TokenType.NUMBER;
import static com.github.rundown.lexer.TokenType.WHITE_SPACE;
import static com.github.rundown.lexer.TokenType.YARD;
import static com.github.rundown.lexer.TokenType.YARD_QUALIFIED;

import com.github.rundown.lexer.Token;
import com.github.rundown.lexer.TokenType;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.Expression.Distance;
import java.util.Set;

public class DistanceParser {
  private static final Set<TokenType> DISTANCE_UNITS = Set.of(METER, MILE, KILOMETER, YARD);
  private static final Set<TokenType> DISTANCE_UNITS_QUALIFIED = Set.of(
      METER_QUALIFIED, MILE_QUALIFIED, KILOMETER_QUALIFIED, YARD_QUALIFIED);

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
      if (parser.match(DISTANCE_UNITS)) {
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
      if (parser.match(WHITE_SPACE)) {
        if (parser.match(DISTANCE_UNITS_QUALIFIED)) {
          return new Distance(Double.parseDouble(distance.value()), DistanceUnit.fromTokenType(parser.previous().type()));
        }
      }
    }
    parser.setCurrent(current);
    return null;
  }
}
