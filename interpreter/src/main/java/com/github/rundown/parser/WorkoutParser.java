package com.github.rundown.parser;

import static com.github.rundown.lexer.TokenType.COMMA;
import static com.github.rundown.lexer.TokenType.SEMICOLON;
import static com.github.rundown.lexer.TokenType.WHITE_SPACE;

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
    Action action = actionParser.action();
    if (action != null && !matchSectionEnd()) {
      matchComponentSeparator();
    }
    Metadata metadata = metadataParser.mainMetadata();
    if (metadata != null && !matchSectionEnd()) {
      matchComponentSeparator();
    }
    Target target = targetParser.target();
    if (target != null && !matchSectionEnd()) {
      matchComponentSeparator();
    }
    Recovery recovery = recoveryParser.recovery();
    if (!matchSectionEnd()) {
      throw new RundownParsingException(parser.peek());
    }
    Section section = new Section(action, metadata, target, recovery);
    validateSection(section);
    return section;
  }

  private boolean matchComponentSeparator() {
    boolean matched = parser.match(WHITE_SPACE);
    matched |= parser.match(COMMA);
    parser.match(WHITE_SPACE);
    return matched;
  }

  private boolean matchSectionEnd() {
    int current = parser.getCurrent();
    parser.match(WHITE_SPACE);
    boolean sectionEnd = parser.isAtEnd() || parser.match(SEMICOLON);
    if (!sectionEnd) {
      parser.setCurrent(current);
    }
    return sectionEnd;
  }

  private void validateSection(Section section) {
    if (section.action == null && section.metadata == null && section.target == null && section.recovery == null) {
      throw new RundownSemanticException("A section must have at least one component");
    }

    if (section.action == null && section.metadata == null && section.target != null) {
      throw new RundownSemanticException("Target is not allowed as the main component of a section");
    }
  }
}
