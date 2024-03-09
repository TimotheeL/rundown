package com.github.rundown.parser.time;

import static com.github.rundown.lexer.TokenType.COLON;
import static com.github.rundown.lexer.TokenType.HOUR;
import static com.github.rundown.lexer.TokenType.MINUTE;
import static com.github.rundown.lexer.TokenType.NUMBER;
import static com.github.rundown.lexer.TokenType.SECOND;

import com.github.rundown.lexer.Token;
import com.github.rundown.lexer.TokenType;
import com.github.rundown.parser.Expression.Time;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.RundownParsingException;
import java.util.ArrayList;
import java.util.List;

public class TimeParser {
  private final Parser parser;

  public TimeParser(Parser parser) {
    this.parser = parser;
  }

  public Time time() {
    Time timeWithUnits = timeWithUnits();
    if (timeWithUnits != null) {
      return timeWithUnits;
    }
    return timeWithoutUnits();
  }

  /*
  * Parses a time of the form 00h00mn00s
  *
  * @return a Time object
  * @return null if no match is found
  * @throws TimeFormatException if the time is not in the correct format (e.g too many or too little digit)
  */
  private Time timeWithUnits() {
    Token hour = timeWithUnits(HOUR);
    Token minute = timeWithUnits(MINUTE);
    Token second = timeWithUnits(SECOND);

    if (hour == null && minute == null && second == null) {
      return null;
    }

    Token trailingTime = trailingTime();

    if (trailingTime != null && second != null) {
      throw new RundownParsingException(trailingTime);
    }

    if (minute == null && second == null) {
      minute = trailingTime;
    } else if (second == null) {
      second = trailingTime;
    }

    validateTimeWithUnits(hour, minute, second);

    return new Time(mapNumberTokenToInt(hour), mapNumberTokenToInt(minute), mapNumberTokenToInt(second));
  }

  /*
   * Parses a time of the form 00:00:00
   *
   * @return a Time object
   * @return null if no time is found
   * @throws TimeFormatException if the time is not in the correct format (e.g too many or too little parts)
   */
  private Time timeWithoutUnits() {
    List<Token> times = new ArrayList<>();
    int current = parser.getCurrent();
    if (parser.match(NUMBER)) {
      if (parser.match(COLON)) {
        parser.setCurrent(parser.getCurrent() - 1);
        times.add(parser.previous());
        while (parser.match(COLON) && parser.match(NUMBER)) {
          times.add(parser.previous());
        }
      } else {
        parser.setCurrent(current);
        return null;
      }
    } else {
      parser.setCurrent(current);
      return null;
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
    if (parser.match(NUMBER)) {
      time = parser.previous();
    }
    return time;
  }

  private Token timeWithUnits(TokenType timeUnit) {
    if (parser.match(TokenType.NUMBER)) {
      Token time = parser.previous();
      if (parser.match(timeUnit)) {
        return time;
      }
      parser.setCurrent(parser.getCurrent() - 1);
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
