package com.github.rundown.parser.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.rundown.lexer.Lexer;
import com.github.rundown.lexer.Token;
import com.github.rundown.parser.Expression.Time;
import com.github.rundown.parser.Parser;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TimeParserTest {

  private final Lexer lexer = new Lexer();
  private TimeParser underTest;

  private void setUpParser(String input) {
    List<Token> tokens = lexer.lex(input);
    underTest = new TimeParser(new Parser(tokens));
  }

  @Test
  void canParseTime() {
    // given
    setUpParser("1h02mn03s");

    // when
    Time time = underTest.time();

    // then
    assertThat(time).isEqualToComparingFieldByFieldRecursively(new Time(1, 2, 3));
  }

  @Test
  void canParseHour() {
    // given
    setUpParser("1h");

    // when
    Time time = underTest.time();

    // then
    assertThat(time).isEqualToComparingFieldByFieldRecursively(
        new Time(
            1,
            0,
            0
        ));
  }

  @Test
  void canParseMinute() {
    // given
    setUpParser("6mn");

    // when
    Time time = underTest.time();

    // then
    assertThat(time).isEqualToComparingFieldByFieldRecursively(
        new Time(
            0,
            6,
            0
        ));
  }

  @Test
  void canParseMinuteAndSecond() {
    // given
    setUpParser("6mn06\"");

    // when
    Time time = underTest.time();

    // then
    assertThat(time).isEqualToComparingFieldByFieldRecursively(
        new Time(
            0,
            6,
            6
        ));
  }

  @ParameterizedTest
  @ValueSource(strings = {"02h3mn", "6mn6s", "2h02mn2s", "01h1", "01:1", "1:", "01:01:01:01", "01:000"})
  void throwsOnInvalidTimes(String input) {
    // given
    setUpParser(input);

    // when
    // then
    assertThatThrownBy(underTest::time).isInstanceOf(TimeFormatException.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2h03mn", "123h", "120mn10s", "6mn06s", "2h02mn02s", "01h01", "1'09", "7'", "1mn"})
  void canParseValidTimes(String input) {
    // given
    setUpParser(input);

    // when
    // then - no exception
    underTest.time();
  }

  @ParameterizedTest
  @ValueSource(strings = {"1:02:03", "01:02:03"})
  void canParseTimeWithoutUnits_hours(String input) {
    // given
    setUpParser(input);

    // when
    Time time = underTest.time();

    // then
    assertThat(time).isEqualToComparingFieldByFieldRecursively(new Time(1, 2, 3));
  }

  @ParameterizedTest
  @ValueSource(strings = {"2:00", "02:00"})
  void canParseTimeWithoutUnits_minutes(String input) {
    // given
    setUpParser(input);

    // when
    Time time = underTest.time();

    // then
    assertThat(time).isEqualToComparingFieldByFieldRecursively(new Time(0, 2, 0));
  }
}
