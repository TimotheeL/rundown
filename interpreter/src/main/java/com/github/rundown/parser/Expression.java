package com.github.rundown.parser;

import com.github.rundown.lexer.token.Token;
import java.util.List;

public abstract class Expression {
  public static final class Workout extends Expression {
    public final List<Section> sections;

    Workout(List<Section> sections) {
      this.sections = sections;
    }

    @Override
    public String toString() {
      return "Workout(" + sections + ")";
    }
  }

  public static final class Section extends Expression {
    public final Set set;
    public final Recovery recovery;

    Section(Set set, Recovery recovery) {
      this.set = set;
      this.recovery = recovery;
    }

    @Override
    public String toString() {
      return "Section(" + set + ", " + recovery + ")";
    }
  }

  public static final class Set extends Expression {
    public final List<Rep> reps;

    Set(List<Rep> reps) {
      this.reps = reps;
    }

    @Override
    public String toString() {
      return "Set(" + reps + ")";
    }
  }

  public static final class Rep extends Expression {
    public final Token value;
    public final Token unit;

    Rep(Token value, Token unit) {
      this.value = value;
      this.unit = unit;
    }

    @Override
    public String toString() {
      return "Rep(" + value + ", " + unit + ")";
    }
  }

  public static final class Recovery extends Expression {
    public final Token type;
    public final Rep recoveryRep;

    Recovery(Token type, Rep recoveryRep) {
      this.type = type;
      this.recoveryRep = recoveryRep;
    }

    @Override
    public String toString() {
      return "Recovery(" + type + ", " + recoveryRep + ")";
    }
  }

  public static final class Time extends Expression {
    public final int hour;
    public final int minute;
    public final int second;
    Time(int hour, int minute, int second) {
      this.hour = hour;
      this.minute = minute;
      this.second = second;
    }

    @Override
    public String toString() {
      return "Time(" + hour + ", " + minute + ", " + second + ")";
    }
  }
}
