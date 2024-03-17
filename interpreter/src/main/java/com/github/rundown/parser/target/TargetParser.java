package com.github.rundown.parser.target;

import static com.github.rundown.lexer.TokenType.AT;
import static com.github.rundown.lexer.TokenType.FLOAT;
import static com.github.rundown.lexer.TokenType.NUMBER;
import static com.github.rundown.lexer.TokenType.PARENTHESIS_LEFT;
import static com.github.rundown.lexer.TokenType.PARENTHESIS_RIGHT;
import static com.github.rundown.lexer.TokenType.RACE_PACE;
import static com.github.rundown.lexer.TokenType.SLASH;
import static com.github.rundown.parser.TokenGroups.DISTANCE_UNITS;
import static com.github.rundown.parser.TokenGroups.PREFIXED_TARGETS;
import static com.github.rundown.parser.TokenGroups.SUFFIXED_TARGETS;
import static com.github.rundown.parser.TokenGroups.TARGETS_SPECIFIC_DISTANCES;
import static com.github.rundown.parser.TokenGroups.TARGET_RANGE_SEPARATORS;
import static com.github.rundown.parser.TokenGroups.TIME_UNITS;
import static java.lang.String.format;

import com.github.rundown.lexer.Token;
import com.github.rundown.lexer.TokenType;
import com.github.rundown.parser.Expression;
import com.github.rundown.parser.Expression.Distance;
import com.github.rundown.parser.Expression.FloatValue;
import com.github.rundown.parser.Expression.IntegerValue;
import com.github.rundown.parser.Expression.Pace;
import com.github.rundown.parser.Expression.Speed;
import com.github.rundown.parser.Expression.Target;
import com.github.rundown.parser.Expression.TargetFixed;
import com.github.rundown.parser.Expression.TargetRange;
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
    if (!parser.match(AT)) {
      return null;
    }
    TargetRange targetRange = factoredTargetRange();
    if (targetRange != null) {
      return targetRange;
    }
    TargetValue firstPart = targetValue();
    if (firstPart == null) {
      throw new RundownParsingException(parser.peek());
    }
    if (!parser.match(TARGET_RANGE_SEPARATORS)) {
      return firstPart;
    }
    TokenType separator = parser.previous().type();
    TargetValue secondPart = targetValue();
    if (secondPart == null) {
      throw new RundownParsingException(parser.peek());
    }

    return new TargetRange(firstPart, secondPart, TargetRangeType.fromTokenType(separator));
  }

  private TargetValue targetValue() {
    if (parser.match(TokenGroups.TARGETS_SINGLE_TOKEN)) {
      return new TargetValue(TargetValueType.FIXED, new TargetFixed(TargetFixedType.fromTokenType(parser.previous().type())));
    }

    if (parser.match(TARGETS_SPECIFIC_DISTANCES)) {
      TokenType type = parser.previous().type();
      parser.matchOrThrow(RACE_PACE);
      return new TargetValue(TargetValueType.RACE_PACE, new TargetFixed(TargetFixedType.fromTokenType(type)));
    }

    Distance distance = distanceParser.distanceUnqualified();
    if (distance != null) {
      if (parser.match(RACE_PACE)) {
        return new TargetValue(TargetValueType.RACE_PACE, distance);
      }
      parser.matchOrThrow(SLASH);
      TimeUnit timeUnit = TimeUnit.fromTokenType(parser.matchOrThrow(TIME_UNITS).type());
      return new TargetValue(TargetValueType.SPEED, new Speed(distance, timeUnit));
    }

    if (parser.match(TokenGroups.PREFIXED_TARGETS)) {
      Token type = parser.previous();
      return prefixedTarget(type);
    }

    TargetValue value = suffixedTarget();
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

  private TargetValue prefixedTarget(Token type) {
    return switch (type.type()) {
      case GAP -> new TargetValue(TargetValueType.GAP, pace());
      case RPE, ZONE -> {
        int value = Integer.parseInt(parser.matchOrThrow(TokenType.NUMBER).value());
        yield new TargetValue(TargetValueType.fromTokenType(type.type()), new IntegerValue(value));
      }
      default -> throw new IllegalStateException(format("Token type %s is not a prefixed target", type.type()));
    };
  }

  private TargetValue suffixedTarget() {
    int current = parser.getCurrent();
    Distance distance = distanceParser.distanceUnqualified();
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
    Distance distance = distanceParser.distanceUnqualified();
    if (distance == null || !parser.match(SLASH)) {
      parser.setCurrent(current);
      return null;
    }

    TimeUnit timeUnit = TimeUnit.fromTokenType(parser.matchOrThrow(TIME_UNITS).type());
    return new Speed(distance, timeUnit);
  }

  private Pace pace() {
    int current = parser.getCurrent();
    Time time = timeParser.time();
    if (time == null || !parser.match(SLASH)) {
      parser.setCurrent(current);
      return null;
    }

    DistanceUnit distanceUnit = DistanceUnit.fromTokenType(parser.matchOrThrow(TokenGroups.DISTANCE_UNITS).type());
    return new Pace(time, distanceUnit);
  }

  private TargetRange factoredTargetRange() {
    TargetRange prefixedTargetRange = prefixedFactoredTargetRange();
    if (prefixedTargetRange != null) {
      return prefixedTargetRange;
    }
    if (!parser.match(PARENTHESIS_LEFT)) {
      return null;
    }
    Time time = timeParser.time();
    if (time != null) {
      return factoredPaceRange(time);
    }
    Distance distance = distanceParser.distanceUnqualified();
    if (distance != null) {
      return factoredRacePaceRange(distance);
    }
    if (parser.match(TARGETS_SPECIFIC_DISTANCES)) {
      return factoredRacePaceRange(new TargetFixed(TargetFixedType.fromTokenType(parser.previous().type())));
    }
    if (parser.match(FLOAT)) {
      return factoredNumberRange(new FloatValue(Double.parseDouble(parser.previous().value())));
    }
    if (parser.match(NUMBER)) {
      return factoredNumberRange(new IntegerValue(Integer.parseInt(parser.previous().value())));
    }
    throw new RundownParsingException(parser.peek());
  }

  private TargetRange factoredNumberRange(Expression value1) {
    TargetRangeType type = TargetRangeType.fromTokenType(parser.matchOrThrow(TARGET_RANGE_SEPARATORS).type());
    Expression value2 = parser.match(FLOAT)
        ? new FloatValue(Double.parseDouble(parser.previous().value()))
        : new IntegerValue(Integer.parseInt(parser.matchOrThrow(NUMBER).value()));
    parser.matchOrThrow(PARENTHESIS_RIGHT);
    if (value1 instanceof FloatValue || value2 instanceof FloatValue) {
      return factoredSpeedRange(value1, value2, type);
    }
    if (parser.match(TIME_UNITS)) {
      TimeUnit timeUnit = TimeUnit.fromTokenType(parser.previous().type());
      return new TargetRange(
          new TargetValue(TargetValueType.TIME, mapIntegerValueToTime((IntegerValue) value1, timeUnit)),
          new TargetValue(TargetValueType.TIME, mapIntegerValueToTime((IntegerValue) value2, timeUnit)),
          type
      );
    }

    TargetValueType targetValueType = TargetValueType.fromTokenType(parser.matchOrThrow(SUFFIXED_TARGETS).type());
    return new TargetRange(
        new TargetValue(targetValueType, value1),
        new TargetValue(targetValueType, value2),
        type
    );
  }

  private TargetRange factoredSpeedRange(Expression value1, Expression value2, TargetRangeType type) {
    DistanceUnit distanceUnit = DistanceUnit.fromTokenType(parser.matchOrThrow(DISTANCE_UNITS).type());
    parser.matchOrThrow(SLASH);
    TimeUnit timeUnit = TimeUnit.fromTokenType(parser.matchOrThrow(TIME_UNITS).type());
    return new TargetRange(
        new TargetValue(TargetValueType.SPEED, new Speed(new Distance(getDoubleFromNumberValue(value1), distanceUnit), timeUnit)),
        new TargetValue(TargetValueType.SPEED, new Speed(new Distance(getDoubleFromNumberValue(value2), distanceUnit), timeUnit)),
        type
    );
  }

  private TargetRange factoredRacePaceRange(Expression value1) {
    TargetRangeType type = TargetRangeType.fromTokenType(parser.matchOrThrow(TARGET_RANGE_SEPARATORS).type());
    Distance distance2 = distanceParser.distanceUnqualified();
    TargetRange targetRange;
    if (distance2 != null) {
      targetRange = new TargetRange(
          new TargetValue(TargetValueType.RACE_PACE, value1),
          new TargetValue(TargetValueType.RACE_PACE, distance2),
          type
      );
    } else {
      TargetFixedType targetFixedType = TargetFixedType.fromTokenType(parser.matchOrThrow(TARGETS_SPECIFIC_DISTANCES).type());
      targetRange = new TargetRange(
          new TargetValue(TargetValueType.RACE_PACE, value1),
          new TargetValue(TargetValueType.RACE_PACE, new TargetFixed(targetFixedType)),
          type
      );
    }
    parser.matchOrThrow(PARENTHESIS_RIGHT);
    parser.matchOrThrow(RACE_PACE);
    return targetRange;
  }

  private TargetRange factoredPaceRange(Time value1) {
    TargetRangeType type = TargetRangeType.fromTokenType(parser.matchOrThrow(TARGET_RANGE_SEPARATORS).type());
    Time value2 = timeParser.time();
    if (value2 == null) {
      throw new RundownParsingException(parser.peek());
    }
    parser.matchOrThrow(PARENTHESIS_RIGHT);
    parser.matchOrThrow(SLASH);
    DistanceUnit distanceUnit = DistanceUnit.fromTokenType(parser.matchOrThrow(DISTANCE_UNITS).type());
    return new TargetRange(
        new TargetValue(TargetValueType.PACE, new Pace(value1, distanceUnit)),
        new TargetValue(TargetValueType.PACE, new Pace(value2, distanceUnit)),
        type
    );
  }

  private TargetRange prefixedFactoredTargetRange() {
    int current = parser.getCurrent();
    if (!parser.match(PREFIXED_TARGETS)) {
      return null;
    }
    TargetValueType targetType = TargetValueType.fromTokenType(parser.previous().type());
    if (!parser.match(PARENTHESIS_LEFT)) {
      parser.setCurrent(current);
      return null;
    }
    if (targetType == TargetValueType.GAP) {
      return factoredGapRange();
    }
    int value1 = Integer.parseInt(parser.matchOrThrow(NUMBER).value());
    TargetRangeType rangeType = TargetRangeType.fromTokenType(parser.matchOrThrow(TARGET_RANGE_SEPARATORS).type());
    int value2 = Integer.parseInt(parser.matchOrThrow(NUMBER).value());
    parser.matchOrThrow(PARENTHESIS_RIGHT);
    return new TargetRange(
        new TargetValue(targetType, new IntegerValue(value1)),
        new TargetValue(targetType, new IntegerValue(value2)),
        rangeType
    );
  }

  private TargetRange factoredGapRange() {
    Time time1 = timeParser.time();
    TargetRangeType rangeType = TargetRangeType.fromTokenType(parser.matchOrThrow(TARGET_RANGE_SEPARATORS).type());
    Time time2 = timeParser.time();
    parser.matchOrThrow(PARENTHESIS_RIGHT);
    parser.matchOrThrow(SLASH);
    DistanceUnit distanceUnit = DistanceUnit.fromTokenType(parser.matchOrThrow(DISTANCE_UNITS).type());
    return new TargetRange(
        new TargetValue(TargetValueType.GAP, new Pace(time1, distanceUnit)),
        new TargetValue(TargetValueType.GAP, new Pace(time2, distanceUnit)),
        rangeType
    );
  }

  private double getDoubleFromNumberValue(Expression expression) {
    return switch (expression) {
      case FloatValue floatValue -> floatValue.value;
      case IntegerValue intValue -> (double) intValue.value;
      default -> throw new IllegalStateException("Unexpected value: " + expression);
    };
  }

  private Time mapIntegerValueToTime(IntegerValue value, TimeUnit timeUnit) {
    return switch (timeUnit) {
      case HOUR -> new Time(value.value, 0, 0);
      case MINUTE -> new Time(0, value.value, 0);
      case SECOND -> new Time(0, 0, value.value);
    };
  }
}
