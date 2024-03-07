package com.github.rundown.parser;

import static com.github.rundown.lexer.token.TokenType.COLON;
import static com.github.rundown.lexer.token.TokenType.HOUR;
import static com.github.rundown.lexer.token.TokenType.MINUTE;
import static com.github.rundown.lexer.token.TokenType.NUMBER;
import static com.github.rundown.lexer.token.TokenType.SECOND;

import com.github.rundown.lexer.token.Token;
import com.github.rundown.lexer.token.TokenType;
import com.github.rundown.parser.Expression.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TimeParser extends WorkoutParser {

  private final static Set<TokenType> TIME_UNITS = Set.of(HOUR, MINUTE, SECOND);

  public TimeParser(List<Token> tokens) {
    super(tokens);
  }

  public Time time() {
    Time timeWithUnits = timeWithUnits();
    if (timeWithUnits != null) {
      return timeWithUnits;
    }
    return timeWithoutUnits();
  }

  private Time timeWithUnits() {
    Token hour = timeWithUnits(HOUR);
    Token minute = timeWithUnits(MINUTE);
    Token second = timeWithUnits(SECOND);

    if (hour == null && minute == null && second == null) {
      return null;
    }

    Token trailingTime = trailingTime();

    if (trailingTime != null && second != null) {
      throw new TimeFormatException("Unexpected token: " + trailingTime.value() + " at position: " + trailingTime.startPosition());
    }

    if (minute == null && second == null) {
      minute = trailingTime;
    } else if (second == null) {
      second = trailingTime;
    }

    validateTimeWithUnits(hour, minute, second);

    return new Time(mapNumberTokenToInt(hour), mapNumberTokenToInt(minute), mapNumberTokenToInt(second));
  }

  private Time timeWithoutUnits() {
    List<Token> times = new ArrayList<>();
    if (match(NUMBER)) {
      if (match(COLON)) {
        backtrack();
        times.add(previous());
        while (match(COLON) && match(NUMBER)) {
          times.add(previous());
        }
      }
    }

    validateTimeWithoutUnits(times);

    if (times.size() != 3) {
      // For mm:ss representations, add a token representing hours
      times.addFirst(new Token(NUMBER, "00", -1));
    }

    return new Time(mapNumberTokenToInt(times.get(0)), mapNumberTokenToInt(times.get(1)), mapNumberTokenToInt(times.get(2)));
  }

  private Token trailingTime() {
    Token time = null;
    if (match(NUMBER)) {
      if (!match(TIME_UNITS)) {
        time = previous();
      }
    }
    return time;
  }

  private Token timeWithUnits(TokenType timeUnit) {
    if (match(TokenType.NUMBER)) {
      Token time = previous();
      if (match(timeUnit)) {
        return time;
      }
      backtrack();
    }

    return null;
  }

  private void validateTimeWithUnits(Token hour, Token minute, Token second) {
    if (second != null && second.value().length() != 2 && (minute != null || hour != null)) {
      throw new TimeFormatException("Minor time units must have 2 digits");
    }

    if (minute != null && minute.value().length() != 2 && hour != null) {
      throw new TimeFormatException("Minor time units must have 2 digits");
    }
  }

  private void validateTimeWithoutUnits(List<Token> times) {
    if (times.size() < 2 || times.size() > 3) {
      throw new TimeFormatException("Time values can only have 2 or 3 parts (mm:ss or hh:mm:ss)");
    }

    for (int i = 1; i < times.size(); i++) {
      if (times.get(i).value().length() != 2) {
        throw new TimeFormatException("Minor time units must have 2 digits");
      }
    }
  }

  private int mapNumberTokenToInt(Token token) {
    if (token == null) {
      return 0;
    }
    return Integer.parseInt(token.value());
  }
}
