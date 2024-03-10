package com.github.rundown.parser;

import com.github.rundown.lexer.Token;
import com.github.rundown.lexer.TokenType;
import com.github.rundown.parser.distance.DistanceUnit;
import java.util.List;

public abstract class Expression {

  public interface Visitor<R> {
    R visitExpression(Expression expression);
    R visitWorkout(Workout workout);
    R visitSection(Section section);
    R visitAction(Action action);
    R visitSet(Set set);
    R visitRep(Rep rep);
    R visitMetadata(Metadata metadata);
    R visitTargetValue(TargetValue targetValue);
    R visitTargetToken(TargetToken targetToken);
    R visitTargetRange(TargetRange targetRange);
    R visitRecovery(Recovery recovery);
    R visitTime(Time time);
    R visitDistance(Distance distance);
    R visitPace(Pace pace);
    R visitSpeed(Speed speed);
    R visitIntegerValue(IntegerValue integerValue);
  }

  public static final class Workout extends Expression {
    public final List<Section> sections;

    public Workout(List<Section> sections) {
      this.sections = sections;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitWorkout(this);
    }
  }

  public static final class Section extends Expression {
    public final Action action;
    public final Metadata metadata;
    public final Target target;
    public final Recovery recovery;

    public Section(Action action, Metadata metadata, Target target, Recovery recovery) {
      this.action = action;
      this.metadata = metadata;
      this.target = target;
      this.recovery = recovery;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitSection(this);
    }
  }

  public static abstract class Action extends Expression {
  }

  public static final class Set extends Action {
    public final Integer multiplier;
    public final Section section;

    public Set(Integer multiplier, Section section) {
      this.multiplier = multiplier;
      this.section = section;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitSet(this);
    }
  }

  public static abstract class Rep extends Action {
  }

  public static class Metadata extends Expression {
    public final List<Token> keywords;

    public Metadata(List<Token> keywords) {
      this.keywords = keywords;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitMetadata(this);
    }
  }

  public static class MetadataMultiple extends Metadata {
    public final int multiplier;

    public MetadataMultiple(int multiplier, List<Token> keywords) {
      super(keywords);
      this.multiplier = multiplier;
    }
  }

  public static abstract class Target extends Expression {
  }

  public static abstract class TargetSinglePart extends Target {
  }

  public static class TargetToken extends TargetSinglePart {
    public final TokenType targetType;

    public TargetToken(TokenType targetType) {
      this.targetType = targetType;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitTargetToken(this);
    }
  }

  public static final class TargetValue extends TargetSinglePart {
    public final Type type;
    public final Expression value;

    public TargetValue(Type type, Expression value) {
      this.type = type;
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitTargetValue(this);
    }

    public enum Type {
      GAP,
      HEARTRATE,
      PACE,
      RACE_PACE,
      POWER,
      RPE,
      SPEED,
      STEPS,
      TIME,
      ZONE;

      public static Type fromTokenType(TokenType tokenType) {
        return switch (tokenType) {
          case GAP -> GAP;
          case BPM -> HEARTRATE;
          case RACE_PACE -> RACE_PACE;
          case WATTS -> POWER;
          case RPE -> RPE;
          case SPM -> STEPS;
          case ZONE -> ZONE;
          default -> throw new IllegalArgumentException("Invalid token type: " + tokenType);
        };
      }
    }
  }

  public static final class TargetRange extends Target {
    public final TargetSinglePart lowerBound;
    public final TargetSinglePart upperBound;
    public final RangeType type;

    public TargetRange(TargetSinglePart lowerBound, TargetSinglePart upperBound, RangeType type) {
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
      this.type = type;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitTargetRange(this);
    }

    public enum RangeType {
      RANGE,
      PROGRESSION_REP,
      PROGRESSION_SET;

      public static RangeType fromTokenType(TokenType tokenType) {
        return switch (tokenType) {
          case MINUS -> RANGE;
          case PROGRESSION_REP -> PROGRESSION_REP;
          case PROGRESSION_SET -> PROGRESSION_SET;
          default -> throw new IllegalArgumentException("Invalid token type: " + tokenType);
        };
      }
    }
  }

  public static final class Recovery extends Expression {
    public final Token type;
    public final Rep recoveryRep;

    public Recovery(Token type, Rep recoveryRep) {
      this.type = type;
      this.recoveryRep = recoveryRep;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitRecovery(this);
    }
  }

  public static final class Time extends Rep {
    public final int hour;
    public final int minute;
    public final int second;

    public Time(int hour, int minute, int second) {
      this.hour = hour;
      this.minute = minute;
      this.second = second;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitTime(this);
    }
  }

  public static final class Distance extends Rep {
    public final double value;
    public final DistanceUnit unit;

    public Distance(double value, DistanceUnit unit) {
      this.value = value;
      this.unit = unit;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitDistance(this);
    }
  }

  public static final class Pace extends Expression {
    public final Time time;
    public final Token distanceUnit;

    public Pace(Time time, Token distanceUnit) {
      this.time = time;
      this.distanceUnit = distanceUnit;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitPace(this);
    }
  }

  public static final class Speed extends Expression {
    public final Distance distance;
    public final Token timeUnit;

    public Speed(Distance distance, Token timeUnit) {
      this.distance = distance;
      this.timeUnit = timeUnit;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitSpeed(this);
    }
  }

  public static final class IntegerValue extends Expression {
    public final int value;

    public IntegerValue(int value) {
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitIntegerValue(this);
    }
  }

  public abstract <R> R accept(Visitor<R> visitor);
}
