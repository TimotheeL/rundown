package com.github.rundown.parser.action;

import com.github.rundown.lexer.TokenType;
import com.github.rundown.parser.Expression;
import com.github.rundown.parser.Expression.Action;
import com.github.rundown.parser.Expression.Distance;
import com.github.rundown.parser.Expression.Section;
import com.github.rundown.parser.Expression.Set;
import com.github.rundown.parser.Expression.Time;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.RundownParsingException;
import com.github.rundown.parser.distance.DistanceParser;
import com.github.rundown.parser.time.TimeParser;

public class ActionParser {

  private final Parser parser;
  private final TimeParser timeParser;
  private final DistanceParser distanceParser;

  public ActionParser(
      Parser parser,
      TimeParser timeParser,
      DistanceParser distanceParser
  ) {
    this.parser = parser;
    this.timeParser = timeParser;
    this.distanceParser = distanceParser;
  }

  public Action action() {
    Action timeOrDistance = timeOrDistance();

    if (timeOrDistance != null) {
      return timeOrDistance;
    }

    if (parser.match(TokenType.NUMBER)) {
      int multiplier = Integer.parseInt(parser.previous().value());
      parser.match(TokenType.WHITE_SPACE);
      if (parser.match(TokenType.MULTIPLIER)) {
        parser.match(TokenType.WHITE_SPACE);
        timeOrDistance = timeOrDistance();
        if (timeOrDistance != null) {
          return new Set(multiplier, new Section(timeOrDistance, null, null));
        }
      }
    }

    throw new RundownParsingException(parser.peek());
  }

  private Action timeOrDistance() {
    Time time = timeParser.time();
    if (time != null) {
      return time;
    }

    Distance distance = distanceParser.distance();
    if (distance != null) {
      return distance;
    }

    return null;
  }
}
