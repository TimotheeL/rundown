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
import com.github.rundown.parser.Expression.TargetFixed;
import com.github.rundown.parser.Expression.TargetRange;
import com.github.rundown.parser.Expression.TargetSinglePart;
import com.github.rundown.parser.Expression.TargetValue;
import com.github.rundown.parser.Expression.Time;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.RundownParsingException;
import com.github.rundown.parser.TokenGroups;
import com.github.rundown.parser.distance.DistanceParser;
import com.github.rundown.parser.distance.DistanceUnit;
import com.github.rundown.parser.time.TimeParser;
import com.github.rundown.parser.time.TimeUnit;

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
      TargetSinglePart targetSinglePart = targetSinglePart();
      if (targetSinglePart != null) {
        return targetSinglePart;
      }
      throw new RundownParsingException(parser.peek());
    }
    return null;
  }

  private TargetSinglePart targetSinglePart() {
    if (parser.match(TokenGroups.TARGETS_SINGLE_TOKEN)) {
      return new TargetFixed(TargetFixedType.fromTokenType(parser.previous().type()));
    }

    if (parser.match(TokenType.M)) {
      if (parser.match(RACE_PACE)) {
        return new TargetFixed(TargetFixedType.MP);
      }
      throw new RundownParsingException(parser.peek());
    }

    if (parser.match(TokenGroups.PREFIXED_TARGETS)) {
      Token type = parser.previous();
      return prefixedTargetValue(type);
    }

    TargetValue value = suffixedTargetValue();
    if (value != null) {
      return value;
    }

    Speed speed = speed();
    if (speed != null) {
      return new TargetValue(TargetValueType.SPEED, speed);
    }

    Pace pace = pace();
    if (pace != null) {
      return new TargetValue(TargetValueType.PACE, pace);
    }

    Time time = timeParser.time();
    if (time != null) {
      return new TargetValue(TargetValueType.TIME, time);
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
          return new TargetRange(firstPart, secondPart, TargetRangeType.fromTokenType(separator));
        }
        throw new RundownParsingException(parser.peek());
      }
    }
    parser.setCurrent(current);
    return null;
  }

  private TargetValue prefixedTargetValue(Token type) {
    return switch (type.type()) {
      case GAP -> new TargetValue(TargetValueType.fromTokenType(type.type()), pace());
      case RPE, ZONE -> {
        if (parser.match(TokenType.NUMBER)) {
          yield new TargetValue(TargetValueType.fromTokenType(type.type()), new IntegerValue(Integer.parseInt(parser.previous().value())));
        }
        throw new RundownParsingException(parser.peek());
      }
      default -> throw new RundownParsingException(parser.peek());
    };
  }

  private TargetValue suffixedTargetValue() {
    int current = parser.getCurrent();
    Distance distance = distanceParser.distance();
    if (distance != null) {
      if (parser.match(RACE_PACE)) {
        return new TargetValue(TargetValueType.RACE_PACE, distance);
      }
      parser.setCurrent(current);
      return null;
    }

    if (parser.match(NUMBER)) {
      Token value = parser.previous();
      if (parser.match(TokenGroups.SUFFIXED_TARGETS)) {
        return new TargetValue(TargetValueType.fromTokenType(parser.previous().type()), new IntegerValue(Integer.parseInt(value.value())));
      }
    }

    parser.setCurrent(current);
    return null;
  }

  private Speed speed() {
    int current = parser.getCurrent();
    Distance distance = distanceParser.distance();
    if (distance == null || !parser.match(SLASH)) {
      parser.setCurrent(current);
      return null;
    }

    if (parser.match(TokenGroups.TIME_UNITS)) {
      return new Speed(distance, TimeUnit.fromTokenType(parser.previous().type()));
    }

    throw new RundownParsingException(parser.peek());
  }

  private Pace pace() {
    int current = parser.getCurrent();
    Time time = timeParser.time();
    if (time == null || !parser.match(SLASH)) {
      parser.setCurrent(current);
      return null;
    }

    if (parser.match(TokenGroups.DISTANCE_UNITS)) {
      return new Pace(time, DistanceUnit.fromTokenType(parser.previous().type()));
    }

    throw new RundownParsingException(parser.peek());
  }
}
