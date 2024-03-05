package com.github.rundown.parser;

import static com.github.rundown.lexer.token.TokenType.COMMA;
import static com.github.rundown.lexer.token.TokenType.EOF;
import static com.github.rundown.lexer.token.TokenType.EQUAL;
import static com.github.rundown.lexer.token.TokenType.HOUR;
import static com.github.rundown.lexer.token.TokenType.KILOMETER;
import static com.github.rundown.lexer.token.TokenType.METER;
import static com.github.rundown.lexer.token.TokenType.MILE;
import static com.github.rundown.lexer.token.TokenType.MINUTE;
import static com.github.rundown.lexer.token.TokenType.MULTIPLIER;
import static com.github.rundown.lexer.token.TokenType.NUMBER;
import static com.github.rundown.lexer.token.TokenType.RECOVERY_JOG;
import static com.github.rundown.lexer.token.TokenType.RECOVERY_STATIC;
import static com.github.rundown.lexer.token.TokenType.RECOVERY_WALK;
import static com.github.rundown.lexer.token.TokenType.SECOND;
import static com.github.rundown.lexer.token.TokenType.SEMICOLON;
import static com.github.rundown.lexer.token.TokenType.YARD;

import com.github.rundown.lexer.token.Token;
import com.github.rundown.lexer.token.TokenType;
import com.github.rundown.parser.Expression.Recovery;
import com.github.rundown.parser.Expression.Rep;
import com.github.rundown.parser.Expression.Section;
import com.github.rundown.parser.Expression.Set;
import com.github.rundown.parser.Expression.Workout;
import java.util.ArrayList;
import java.util.List;

public class WorkoutParser extends AbstractParser<Workout> {

  java.util.Set<TokenType> DISTANCE_UNITS = java.util.Set.of(METER, MILE, KILOMETER, YARD);
  java.util.Set<TokenType> TIME_UNITS = java.util.Set.of(SECOND, MINUTE, HOUR);

  public WorkoutParser(List<Token> tokens) {
    super(tokens);
  }

  @Override
  public Workout parse() {
    List<Section> sections = new ArrayList<>();
    sections.add(section());
    while (peek().type() != EOF) {
      sections.add(section());
    }
    return new Workout(sections);
  }

  private Section section() {
    Set set = set();
    match(COMMA);
    if (match(RECOVERY_JOG, RECOVERY_STATIC, RECOVERY_WALK)) {
      Recovery recovery = recovery();
      match(SEMICOLON, EOF);
      return new Section(set, recovery);
    }
    if (match(SEMICOLON, EOF)) {
      return new Section(set, null);
    }

    throw new RundownParsingException(peek());
  }

  private Set set() {
    if (match(NUMBER)) {
      int value = Integer.parseInt(previous().value());
      if (match(MULTIPLIER)) {
        Rep rep = rep();
        List<Rep> reps = new ArrayList<>();
        for (int i = 0; i < value; i++) {
          reps.add(rep);
        }
        return new Set(reps);
      }
    }

    throw new RundownParsingException(peek());
  }

  private Rep rep() {
    if (match(NUMBER)) {
      Token value = previous();
      if (match(DISTANCE_UNITS) || match(TIME_UNITS)) {
        return new Rep(value, previous());
      }
    }

    throw new RundownParsingException(peek());
  }

  private Recovery recovery() {
    Token type = previous();

    if (match(EQUAL)) {
      Rep recoveryRep = rep();
      return new Recovery(type, recoveryRep);
    }

    throw new RundownParsingException(peek());
  }
}
