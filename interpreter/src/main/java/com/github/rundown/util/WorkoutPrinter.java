package com.github.rundown.util;

import com.github.rundown.lexer.Token;
import com.github.rundown.parser.Expression;
import com.github.rundown.parser.Expression.Action;
import com.github.rundown.parser.Expression.Distance;
import com.github.rundown.parser.Expression.IntegerValue;
import com.github.rundown.parser.Expression.Metadata;
import com.github.rundown.parser.Expression.MetadataMultiple;
import com.github.rundown.parser.Expression.Pace;
import com.github.rundown.parser.Expression.Recovery;
import com.github.rundown.parser.Expression.Rep;
import com.github.rundown.parser.Expression.Section;
import com.github.rundown.parser.Expression.Set;
import com.github.rundown.parser.Expression.Speed;
import com.github.rundown.parser.Expression.Target;
import com.github.rundown.parser.Expression.TargetFixed;
import com.github.rundown.parser.Expression.TargetRange;
import com.github.rundown.parser.Expression.TargetValue;
import com.github.rundown.parser.Expression.Time;
import com.github.rundown.parser.Expression.Visitor;
import com.github.rundown.parser.Expression.Workout;
import java.util.stream.Collectors;

public class WorkoutPrinter implements Visitor<String> {

  private int indentationLevel;

  public WorkoutPrinter() {
    this.indentationLevel = 0;
  }

  public String print(Expression expression) {
    indentationLevel = 0;
    return expression.accept(this);
  }

  public String visitExpression(Expression expression) {
    return expression.accept(this);
  }

  @Override
  public String visitWorkout(Workout workout) {
    return workout.sections.stream().map(
        section -> this.visitSection(section) + "\n"
    ).collect(Collectors.joining());
  }

  @Override
  public String visitSection(Section section) {
    String sectionString = "";
    String tabs = "\t".repeat(indentationLevel);
    indentationLevel++;
    if (section.action != null) {
      sectionString += "\n" + tabs + "Action:\n";
      sectionString += tabs + "\t" + visitAction(section.action);
    }

    if (section.metadata != null) {
      sectionString += "\n" + tabs + "Metadata:\n";
      sectionString += tabs + "\t" + visitMetadata(section.metadata);
    }

    if (section.target != null) {
      sectionString += "\n" + tabs + "Target:\n";
      sectionString += tabs + "\t" + visitTarget(section.target);
    }

    if (section.recovery != null) {
      sectionString += "\n" + tabs + "Recovery:\n";
      sectionString += tabs + "\t" + visitRecovery(section.recovery);
    }

    indentationLevel--;

    return sectionString;
  }

  @Override
  public String visitAction(Action action) {
    if (action == null) {
      return "";
    }

    return switch (action) {
      case Set set -> visitSet(set);
      case Rep rep -> visitRep(rep);
      default -> "";
    };
  }

  @Override
  public String visitSet(Set set) {
    return set.multiplier == null ? "" : set.multiplier + " x " + this.visitSection(set.section);
  }

  @Override
  public String visitRep(Rep rep) {
    return switch (rep) {
      case Distance distance -> visitDistance(distance);
      case Time time -> visitTime(time);
      default -> "";
    };
  }

  @Override
  public String visitMetadata(Metadata metadata) {
    if (metadata == null) {
      return "";
    }

    String keywords = metadata.keywords.stream()
        .map(Token::value)
        .collect(Collectors.joining(" "));

    if (metadata instanceof MetadataMultiple metadataMultiple) {
      return metadataMultiple.multiplier + " x " + keywords;
    }

    return keywords;
  }

  private String visitTarget(Target target) {
    return switch (target) {
      case TargetValue targetValue -> visitTargetValue(targetValue);
      case TargetFixed targetFixed -> visitTargetFixed(targetFixed);
      case TargetRange targetRange -> visitTargetRange(targetRange);
      default -> "";
    };
  }

  @Override
  public String visitTargetValue(TargetValue targetValue) {
    return visitExpression(targetValue.value) + " " + targetValue.type;
  }

  @Override
  public String visitTargetFixed(TargetFixed targetFixed) {
    return switch (targetFixed.targetType) {
      case HMP -> "Half Marathon Pace";
      case LT1 -> "Lactate Threshold 1";
      case LT2 -> "Lactate Threshold 2";
      // case MP -> "Marathon Pace";
      case TEMPO -> "Tempo";
      case VO2_MAX -> "VO2 Max";
      default -> "";
    };
  }

  @Override
  public String visitTargetRange(TargetRange targetRange) {
    String progressinString = "Progression: ";
    progressinString += switch (targetRange.type) {
      case RANGE -> "Range: ";
      case PROGRESSION_REP -> "Each rep from ";
      case PROGRESSION_SET -> "Set from ";
    };
    return progressinString + visitTarget(targetRange.lowerBound) + " to " + visitTarget(
        targetRange.upperBound);
  }

  @Override
  public String visitRecovery(Recovery recovery) {
    if (recovery == null) {
      return "";
    }

    String recoveryString = switch (recovery.type) {
      case JOG -> "Jog";
      case STATIC -> "Stay still";
      case WALK -> "Walk";
    };

    return recoveryString + " " + this.visitSection(recovery.section);
  }

  @Override
  public String visitTime(Time time) {
    return time.hour + ":" + time.minute + ":" + time.second;
  }

  @Override
  public String visitDistance(Distance distance) {
    return distance.value + distance.unit.toString();
  }

  @Override
  public String visitPace(Pace pace) {
    return visitTime(pace.time) + "/" + pace.distanceUnit;
  }

  @Override
  public String visitSpeed(Speed speed) {
    return visitDistance(speed.distance) + "/" + speed.timeUnit;
  }

  @Override
  public String visitIntegerValue(IntegerValue integerValue) {
    return "" + integerValue.value;
  }
}
