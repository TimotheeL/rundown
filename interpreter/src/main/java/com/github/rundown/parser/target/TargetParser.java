package com.github.rundown.parser.target;

import static com.github.rundown.lexer.TokenType.AT;
import static com.github.rundown.lexer.TokenType.NUMBER;
import static com.github.rundown.lexer.TokenType.RACE_PACE;
import static com.github.rundown.lexer.TokenType.SLASH;

import com.github.rundown.lexer.Token;
import com.github.rundown.lexer.TokenType;
import com.github.rundown.parser.Expression.Distance;
import com.github.rundown.parser.Expression.IntegerValue;
import com.github.rundown.parser.Expression.Pace;
import com.github.rundown.parser.Expression.Speed;
import com.github.rundown.parser.Expression.Target;
import com.github.rundown.parser.Expression.TargetRange;
import com.github.rundown.parser.Expression.TargetRange.RangeType;
import com.github.rundown.parser.Expression.TargetSinglePart;
import com.github.rundown.parser.Expression.TargetToken;
import com.github.rundown.parser.Expression.TargetValue;
import com.github.rundown.parser.Expression.Time;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.RundownParsingException;
import com.github.rundown.parser.TokenGroups;
import com.github.rundown.parser.distance.DistanceParser;
import com.github.rundown.parser.time.TimeParser;

public class TargetParser {
  private final Parser parser;
  private final TimeParser timeParser;
  private final DistanceParser distanceParser;

  public TargetParser(Parser parser,
      TimeParser timeParser,
      DistanceParser distanceParser) {
    this.parser = parser;
    this.timeParser = timeParser;
    this.distanceParser = distanceParser;
  }

  public Target target() {
    if (parser.match(AT)) {
      TargetRange range = targetRange();
      if (range != null) {
        return range;
      }
      return targetSinglePart();
    }

    return null;
  }

  private TargetSinglePart targetSinglePart() {
    if (parser.match(TokenGroups.TARGET_TOKENS)) {
      return new TargetToken(parser.previous().type());
    }

    if (parser.match(TokenGroups.PREFIXED_TARGET_TOKENS)) {
      Token type = parser.previous();
      return prefixedTargetValue(type);
    }

    TargetValue value = postfixedTargetValue();
    if (value != null) {
      return value;
    }

    Speed speed = speed();
    if (speed != null) {
      return new TargetValue(TargetValue.Type.SPEED, speed);
    }

    Pace pace = pace();
    if (pace != null) {
      return new TargetValue(TargetValue.Type.PACE, pace);
    }

    Time time = timeParser.time();
    if (time != null) {
      return new TargetValue(TargetValue.Type.TIME, time);
    }

    return null;
  }

  private TargetRange targetRange() {
    int current = parser.getCurrent();
    TargetSinglePart firstPart = targetSinglePart();
    if (firstPart != null) {
      if (parser.match(TokenGroups.TARGET_RANGE_SEPARATORS)) {
        TokenType separator = parser.previous().type();
        TargetSinglePart secondPart = targetSinglePart();
        if (secondPart != null) {
          return new TargetRange(firstPart, secondPart, RangeType.fromTokenType(separator));
        }
        throw new RundownParsingException(parser.peek());
      }
    }
    parser.setCurrent(current);
    return null;
  }

  private TargetValue prefixedTargetValue(Token type) {
    return switch (type.type()) {
      case GAP -> new TargetValue(TargetValue.Type.fromTokenType(type.type()), pace());
      case RPE, ZONE -> {
        if (parser.match(TokenType.NUMBER)) {
          yield new TargetValue(TargetValue.Type.fromTokenType(type.type()), new IntegerValue(Integer.parseInt(parser.previous().value())));
        }
        throw new RundownParsingException(parser.peek());
      }
      default -> throw new RundownParsingException(parser.peek());
    };
  }

  private TargetValue postfixedTargetValue() {
    int current = parser.getCurrent();
    Distance distance = distanceParser.distance();
    if (distance != null) {
      if (parser.match(RACE_PACE)) {
        return new TargetValue(TargetValue.Type.RACE_PACE, distance);
      }
      parser.setCurrent(current);
      return null;
    }

    if (parser.match(NUMBER)) {
      Token value = parser.previous();
      if (parser.match(TokenGroups.POSTFIXED_TARGET_TOKENS)) {
        return new TargetValue(TargetValue.Type.fromTokenType(parser.previous().type()), new IntegerValue(Integer.parseInt(value.value())));
      }
    }

    parser.setCurrent(current);
    return null;
  }

  private Speed speed() {
    Distance distance = distanceParser.distance();
    int current = parser.getCurrent();
    if (distance != null) {
      if (parser.match(SLASH)) {
        if (parser.match(TokenGroups.TIME_UNITS)) {
          return new Speed(distance, parser.previous());
        }
        throw new RundownParsingException(parser.peek());
      }
      parser.setCurrent(current);
    }
    return null;
  }

  private Pace pace() {
    int current = parser.getCurrent();
    Time time = timeParser.time();
    if (time != null) {
      if (parser.match(SLASH)) {
        if (parser.match(TokenGroups.DISTANCE_UNITS)) {
          return new Pace(time, parser.previous());
        }
        throw new RundownParsingException(parser.peek());
      }
      parser.setCurrent(current);
    }
    return null;
  }
}
