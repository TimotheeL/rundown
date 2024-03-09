package com.github.rundown.parser.action;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.rundown.lexer.Lexer;
import com.github.rundown.lexer.Token;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.RundownParsingException;
import com.github.rundown.parser.distance.DistanceParser;
import com.github.rundown.parser.time.TimeParser;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ActionParserTest {

  private final Lexer lexer = new Lexer();

  private ActionParser underTest;

  private void setUpParser(String input) {
    List<Token> tokens = lexer.lex(input);
    Parser parser = new Parser(tokens);
    TimeParser timeParser = new TimeParser(parser);
    DistanceParser distanceParser = new DistanceParser(parser);
    underTest = new ActionParser(parser, timeParser, distanceParser);
  }

  @ParameterizedTest
  @ValueSource(strings = {"3 x 3km", "3km", "3x3km", "3mn", "10 x     1:00"})
  public void canParseValidActions(String input) {
    // given
    setUpParser(input);

    // when
    // then
    // - no exception is thrown
    underTest.action();
  }

  @ParameterizedTest
  @ValueSource(strings = {"3 x R=1mn", "R=1mn"})
  public void throwsOnInvalidAction(String input) {
    // given
    setUpParser(input);

    // when
    // then
    assertThatThrownBy(() -> underTest.action()).isInstanceOf(RundownParsingException.class);
  }
}
