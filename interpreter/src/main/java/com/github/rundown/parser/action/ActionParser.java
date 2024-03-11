package com.github.rundown.parser.action;

import com.github.rundown.lexer.TokenType;
import com.github.rundown.parser.Expression.Action;
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
    int current = parser.getCurrent();

    if (!parser.match(TokenType.NUMBER)) {
      return null;
    }
    int multiplier = Integer.parseInt(parser.previous().value());
    parser.match(TokenType.WHITE_SPACE);
    if (!parser.match(TokenType.MULTIPLIER)) {
      parser.setCurrent(current);
      return null;
    }

    parser.match(TokenType.WHITE_SPACE);
    timeOrDistance = timeOrDistance();
    if (timeOrDistance == null) {
      parser.setCurrent(current);
      return null;
    }

    return new Set(multiplier, new Section(timeOrDistance, null, null, null));
  }

  public Action timeOrDistance() {
    Time time = timeParser.time();
    if (time != null) {
      return time;
    }

    return distanceParser.distance();
  }
}
