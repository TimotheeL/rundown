package com.github.rundown.util;

import com.github.rundown.lexer.Token;
import com.github.rundown.parser.Expression;
import com.github.rundown.parser.Expression.Action;
import com.github.rundown.parser.Expression.Distance;
import com.github.rundown.parser.Expression.Metadata;
import com.github.rundown.parser.Expression.MetadataMultiple;
import com.github.rundown.parser.Expression.Recovery;
import com.github.rundown.parser.Expression.Rep;
import com.github.rundown.parser.Expression.Section;
import com.github.rundown.parser.Expression.Set;
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
      sectionString += "Action:\n";
      sectionString += tabs + "\t" + visitAction(section.action) + "\n";
    }

    if (section.metadata != null) {
      sectionString += tabs + "Metadata:\n";
      sectionString += tabs + "\t" + visitMetadata(section.metadata) + "\n";
    }

    if (section.recovery != null) {
      sectionString += tabs + "Recovery:\n";
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

  @Override
  public String visitRecovery(Recovery recovery) {
    if (recovery == null) {
      return "";
    }

    String recoveryString = switch (recovery.type.type()) {
      case RECOVERY_JOG -> "Jog";
      case RECOVERY_STATIC -> "Stay still";
      case RECOVERY_WALK -> "Walk";
      default -> "";
    };

    return recoveryString + " " + this.visitRep(recovery.recoveryRep);
  }

  @Override
  public String visitTime(Time time) {
    return time.hour + ":" + time.minute + ":" + time.second;
  }

  @Override
  public String visitDistance(Distance distance) {
    return distance.value + distance.unit.toString();
  }
}
