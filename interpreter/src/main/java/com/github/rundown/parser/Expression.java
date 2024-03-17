package com.github.rundown.parser;

import com.github.rundown.lexer.Token;
import com.github.rundown.parser.distance.DistanceUnit;
import com.github.rundown.parser.recovery.RecoveryType;
import com.github.rundown.parser.target.TargetFixedType;
import com.github.rundown.parser.target.TargetRangeType;
import com.github.rundown.parser.target.TargetValueType;
import com.github.rundown.parser.time.TimeUnit;
import java.util.List;

public abstract class Expression {

  public interface Visitor<R> {
    R visitWorkout(Workout workout);
    R visitSection(Section section);
    R visitAction(Action action);
    R visitSet(Set set);
    R visitRep(Rep rep);
    R visitMetadata(Metadata metadata);
    R visitTargetValue(TargetValue targetValue);
    R visitTargetRange(TargetRange targetRange);
    R visitRecovery(Recovery recovery);
    R visitRecoverySection(RecoverySection recoverySection);
    R visitTargetFixed(TargetFixed targetFixed);
    R visitTime(Time time);
    R visitDistance(Distance distance);
    R visitPace(Pace pace);
    R visitSpeed(Speed speed);
    R visitIntegerValue(IntegerValue integerValue);
    R visitFloatValue(FloatValue floatValue);
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

  public static final class TargetValue extends Target {
    public final TargetValueType type;
    public final Expression value;

    public TargetValue(TargetValueType type, Expression value) {
      this.type = type;
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitTargetValue(this);
    }

  }

  public static final class TargetRange extends Target {
    public final TargetValue lowerBound;
    public final TargetValue upperBound;
    public final TargetRangeType type;

    public TargetRange(TargetValue lowerBound, TargetValue upperBound, TargetRangeType type) {
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
      this.type = type;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitTargetRange(this);
    }
  }

  public static class Recovery extends Expression {
    public final RecoveryType type;
    public final RecoverySection section;

    public Recovery(RecoveryType type, RecoverySection section) {
      this.type = type;
      this.section = section;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitRecovery(this);
    }
  }

  public static final class RecoveryCycle extends Recovery {

    public RecoveryCycle(RecoveryType type, RecoverySection section) {
      super(type, section);
    }
  }

  public static final class RecoverySection extends Expression {

    public final Rep rep;
    public final Metadata metadata;
    public final Target target;

    public RecoverySection(Rep rep, Metadata metadata, Target target) {
      this.rep = rep;
      this.metadata = metadata;
      this.target = target;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitRecoverySection(this);
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
    public final DistanceUnit distanceUnit;

    public Pace(Time time, DistanceUnit distanceUnit) {
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
    public final TimeUnit timeUnit;

    public Speed(Distance distance, TimeUnit timeUnit) {
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

  public static final class FloatValue extends Expression {
    public final double value;

    public FloatValue(double value) {
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitFloatValue(this);
    }
  }

  public static final class TargetFixed extends Expression {
    public final TargetFixedType type;

    public TargetFixed(TargetFixedType type) {
      this.type = type;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitTargetFixed(this);
    }
  }

  public abstract <R> R accept(Visitor<R> visitor);
}
