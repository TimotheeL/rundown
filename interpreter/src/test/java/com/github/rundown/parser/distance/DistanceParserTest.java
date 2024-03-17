package com.github.rundown.parser.distance;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.rundown.lexer.Lexer;
import com.github.rundown.lexer.Token;
import com.github.rundown.parser.Expression.Distance;
import com.github.rundown.parser.Expression.Time;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.time.TimeParser;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DistanceParserTest {

  private final Lexer lexer = new Lexer();
  private Parser parser;
  private DistanceParser underTest;

  private void setUpParser(String input) {
    List<Token> tokens = lexer.lex(input);
    parser = new Parser(tokens);
    underTest = new DistanceParser(parser);
  }

  @Test
  void canParseDistance() {
    // given
    setUpParser("3km");

    // when
    Distance distance = underTest.distance();

    // then
    assertThat(distance).usingRecursiveComparison().isEqualTo(
        new Distance(3, DistanceUnit.KILOMETER)
    );
  }

  @Test
  void canParseFloatDistance() {
    // given
    setUpParser("13.1M");

    // when
    Distance distance = underTest.distance();

    // then
    assertThat(distance).usingRecursiveComparison().isEqualTo(
        new Distance(13.1, DistanceUnit.MILE)
    );
  }

  @Test
  void canParseFullyQualifiedDistance() {
    // given
    setUpParser("400 Meter");

    // when
    Distance distance = underTest.distance();

    // then
    assertThat(distance).usingRecursiveComparison().isEqualTo(
        new Distance(400, DistanceUnit.METER)
    );
  }

  @Test
  void noMatch() {
    // given
    setUpParser("400mn");

    // when
    Distance distance = underTest.distance();

    // then
    assertThat(distance).isNull();

    // - the parser should have backtracked and not consumed the tokens,
    // so we should be able to parse the input with another parser
    TimeParser timeParser = new TimeParser(parser);

    assertThat(timeParser.time()).usingRecursiveComparison().isEqualTo(
        new Time(0, 400, 0)
    );
  }
}
