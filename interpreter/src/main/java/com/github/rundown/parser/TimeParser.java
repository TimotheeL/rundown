package com.github.rundown.parser;

import static com.github.rundown.lexer.token.TokenType.HOUR;
import static com.github.rundown.lexer.token.TokenType.MINUTE;
import static com.github.rundown.lexer.token.TokenType.NUMBER;
import static com.github.rundown.lexer.token.TokenType.SECOND;

import com.github.rundown.lexer.token.Token;
import com.github.rundown.lexer.token.TokenType;
import com.github.rundown.parser.Expression.Time;
import java.util.List;
import java.util.Set;

public class TimeParser extends WorkoutParser {

  private final static Set<TokenType> TIME_UNITS = Set.of(HOUR, MINUTE, SECOND);

  public TimeParser(List<Token> tokens) {
    super(tokens);
  }

  public Time time() {
    Token hour = time(HOUR);
    Token minute = time(MINUTE);
    Token second = time(SECOND);
    Token timeWithoutUnit = timeWithoutUnit();

    if (minute == null && second == null) {
      minute = timeWithoutUnit;
    } else if (second == null) {
      second = timeWithoutUnit;
    }

    validateTime(hour, minute, second);

    return new Time(mapToInt(hour), mapToInt(minute), mapToInt(second));
  }

  private Token timeWithoutUnit() {
    Token time = null;
    if (match(NUMBER)) {
      if (!match(TIME_UNITS)) {
        time = previous();
      }
    }
    return time;
  }

  private Token time(TokenType timeUnit) {
    if (match(TokenType.NUMBER)) {
      Token time = previous();
      if (match(timeUnit)) {
        if (time.value().length() > 2) {
          throw new TimeFormatException("Time values can only have 2 digits at most");
        }
        return time;
      }
      backtrack();
    }

    return null;
  }

  private void validateTime(Token hour, Token minute, Token second) {
    if (second != null && second.value().length() != 2 && (minute != null || hour != null)) {
      throw new TimeFormatException("Minor time units must have 2 digits");
    }

    if (minute != null && minute.value().length() != 2 && hour != null) {
      throw new TimeFormatException("Minor time units must have 2 digits");
    }
  }

  private int mapToInt(Token time) {
    if (time == null) {
      return 0;
    }
    return Integer.parseInt(time.value());
  }
}
