package com.github.rundown.parser;

import static com.github.rundown.lexer.TokenType.EOF;
import static com.github.rundown.lexer.TokenType.EQUAL;
import static com.github.rundown.lexer.TokenType.KILOMETER;
import static com.github.rundown.lexer.TokenType.MULTIPLIER;
import static com.github.rundown.lexer.TokenType.NUMBER;

import com.github.rundown.lexer.Token;
import com.github.rundown.parser.Expression.Recovery;
import com.github.rundown.parser.Expression.Rep;
import com.github.rundown.parser.Expression.Section;
import com.github.rundown.parser.Expression.Set;
import com.github.rundown.parser.Expression.Workout;
import com.github.rundown.parser.distance.DistanceParser;
import com.github.rundown.parser.metadata.MetadataParser;
import com.github.rundown.parser.time.TimeParser;
import java.util.ArrayList;
import java.util.List;

public class WorkoutParser {

  private final Parser parser;
  private final TimeParser timeParser;
  private final DistanceParser distanceParser;
  private final MetadataParser metadataParser;

  public WorkoutParser(
      Parser parser,
      TimeParser timeParser,
      DistanceParser distanceParser,
      MetadataParser metadataParser) {
    this.parser = parser;
    this.timeParser = timeParser;
    this.distanceParser = distanceParser;
    this.metadataParser = metadataParser;
  }

  public Workout parse() {
    List<Section> sections = new ArrayList<>();
    sections.add(section());
    while (parser.peek().type() != EOF) {
      sections.add(section());
    }
    return new Workout(sections);
  }

  private Section section() {
//    Set set = set();
//    parser.match(COMMA);
//    if (parser.match(RECOVERY_JOG, RECOVERY_STATIC, RECOVERY_WALK)) {
//      Recovery recovery = recovery();
//      parser.match(SEMICOLON, EOF);
//      return new Section(set, recovery);
//    }
//    if (parser.match(SEMICOLON, EOF)) {
//      return new Section(set, null);
//    }

    throw new RundownParsingException(parser.peek());
  }

  private Set set() {
    if (parser.match(NUMBER)) {
      int value = Integer.parseInt(parser.previous().value());
      if (parser.match(MULTIPLIER)) {
        Rep rep = rep();
        List<Rep> reps = new ArrayList<>();
        for (int i = 0; i < value; i++) {
          reps.add(rep);
        }
        return null;
      }
    }

    throw new RundownParsingException(parser.peek());
  }

  private Rep rep() {
    if (parser.match(NUMBER)) {
      Token value = parser.previous();
      if (parser.match(KILOMETER)) {
        return null; // new Rep(value, parser.previous());
      }
    }

    throw new RundownParsingException(parser.peek());
  }

  private Recovery recovery() {
    Token type = parser.previous();

    if (parser.match(EQUAL)) {
      Rep recoveryRep = rep();
      return new Recovery(type, recoveryRep);
    }

    throw new RundownParsingException(parser.peek());
  }
}
