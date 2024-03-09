package com.github.rundown.parser;

import com.github.rundown.lexer.Token;
import com.github.rundown.parser.distance.DistanceUnit;
import java.util.List;

public abstract class Expression {

  public interface Visitor<R> {
    R visitWorkout(Workout workout);
    R visitSection(Section section);
    R visitAction(Action action);
    R visitSet(Set set);
    R visitRep(Rep rep);
    R visitMetadata(Metadata metadata);
    R visitRecovery(Recovery recovery);
    R visitTime(Time time);
    R visitDistance(Distance distance);
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
    public final Recovery recovery;

    public Section(Action action, Metadata metadata, Recovery recovery) {
      this.action = action;
      this.metadata = metadata;
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

  public abstract <R> R accept(Visitor<R> visitor);
}
