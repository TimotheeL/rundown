package com.github.rundown.parser;

import static com.github.rundown.lexer.TokenType.COMMA;
import static com.github.rundown.lexer.TokenType.EOF;
import static com.github.rundown.lexer.TokenType.SEMICOLON;
import static com.github.rundown.lexer.TokenType.WHITE_SPACE;
import static java.lang.String.format;

import com.github.rundown.parser.Expression.Action;
import com.github.rundown.parser.Expression.Metadata;
import com.github.rundown.parser.Expression.Recovery;
import com.github.rundown.parser.Expression.Section;
import com.github.rundown.parser.Expression.Target;
import com.github.rundown.parser.Expression.Workout;
import com.github.rundown.parser.action.ActionParser;
import com.github.rundown.parser.metadata.MetadataParser;
import com.github.rundown.parser.recovery.RecoveryParser;
import com.github.rundown.parser.target.TargetParser;
import java.util.ArrayList;
import java.util.List;

public class WorkoutParser {

  private final Parser parser;
  private final ActionParser actionParser;
  private final MetadataParser metadataParser;
  private final TargetParser targetParser;
  private final RecoveryParser recoveryParser;

  public WorkoutParser(
      Parser parser,
      ActionParser actionParser,
      MetadataParser metadataParser,
      TargetParser targetParser,
      RecoveryParser recoveryParser
  ) {
    this.parser = parser;
    this.actionParser = actionParser;
    this.metadataParser = metadataParser;
    this.targetParser = targetParser;
    this.recoveryParser = recoveryParser;
  }

  public Workout parse() {
    List<Section> sections = new ArrayList<>();
    sections.add(section());
    while (!parser.isAtEnd()) {
      sections.add(section());
    }
    return new Workout(sections);
  }

  private Section section() {
    parser.match(WHITE_SPACE);
    SectionBuilder sectionBuilder = new SectionBuilder();
    Expression component = nextComponent();
    if (component instanceof Target) {
      throw new RuntimeException("Target is not allowed as the main component of a section");
    }
    int componentOrder = getComponentOrder(component);
    sectionBuilder = addComponentToSection(sectionBuilder, component);

    while (!sectionEnd()) {
      if (!componentSeparator()) {
        throw new RundownParsingException(parser.peek());
      }
      component = nextComponent();
      int nextComponentOrder = getComponentOrder(component);
      if (nextComponentOrder <= componentOrder) {
        throw new RuntimeException("Components must be in the order: Action, Metadata, Target, Recovery");
      }
      componentOrder = nextComponentOrder;
      sectionBuilder = addComponentToSection(sectionBuilder, component);
    }
    return sectionBuilder.build();
  }

  private Expression nextComponent() {
    Action action = actionParser.action();
    if (action != null) {
      return action;
    }
    Metadata metadata = metadataParser.mainMetadata();
    if (metadata != null) {
      return metadata;
    }
    Target target = targetParser.target();
    if (target != null) {
      return target;
    }
    Recovery recovery = recoveryParser.recovery();
    if (recovery != null) {
      return recovery;
    }
    throw new IllegalStateException(format("Unable to identify workout component starting with %s", parser.peek()));
  }

  private boolean componentSeparator() {
    boolean matched = parser.match(WHITE_SPACE);
    matched |= parser.match(COMMA);
    parser.match(WHITE_SPACE);
    return matched;
  }

  private boolean sectionEnd() {
    int current = parser.getCurrent();
    parser.match(WHITE_SPACE);
    boolean sectionEnd = parser.isAtEnd() || parser.match(SEMICOLON);
    if (!sectionEnd) {
      parser.setCurrent(current);
    }
    return sectionEnd;
  }

  private int getComponentOrder(Expression component) {
    return switch (component) {
      case Action ignored -> 1;
      case Metadata ignored -> 2;
      case Target ignored -> 3;
      case Recovery ignored -> 4;
      default -> throw new IllegalArgumentException("Unexpected value: " + component);
    };
  }

  private SectionBuilder addComponentToSection(SectionBuilder sectionBuilder, Expression component) {
    return switch (component) {
      case Action action -> sectionBuilder.action(action);
      case Metadata metadata -> sectionBuilder.metadata(metadata);
      case Target target -> sectionBuilder.target(target);
      case Recovery recovery -> sectionBuilder.recovery(recovery);
      case null, default -> throw new IllegalArgumentException("Unexpected value: " + component);
    };
  }
}
