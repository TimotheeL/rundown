package com.github.rundown.parser.target;


import static org.assertj.core.api.Assertions.assertThat;

import com.github.rundown.lexer.Lexer;
import com.github.rundown.lexer.Token;
import com.github.rundown.lexer.TokenType;
import com.github.rundown.parser.Expression.Target;
import com.github.rundown.parser.Expression.TargetToken;
import com.github.rundown.parser.Expression.TargetValue;
import com.github.rundown.parser.Expression.TargetValue.Type;
import com.github.rundown.parser.Expression.Time;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.distance.DistanceParser;
import com.github.rundown.parser.time.TimeParser;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class TargetParserTest {

  private final Lexer lexer = new Lexer();

  private TargetParser underTest;

  private void setUpParser(String input) {
    List<Token> tokens = lexer.lex(input);
    Parser parser = new Parser(tokens);
    TimeParser timeParser = new TimeParser(parser);
    DistanceParser distanceParser = new DistanceParser(parser);
    underTest = new TargetParser(parser, timeParser, distanceParser);
  }

  @ParameterizedTest
  @ValueSource(strings = {"@VO2max", "@rpe10", "@10kP", "@HMP",
      "@4:40/km", "@18km/h", "@Z5", "@LT1", "@LT2", "@gap5:00/km",
      "@180spm", "@180bpm"})
  void canParseTargetPaces(String input) {
    // given
    setUpParser(input);

    // when
    underTest.target();

    // then
    // - no exception is thrown
  }

  @ParameterizedTest
  @MethodSource("targetTokens")
  void canParseTargetTokens(String input, TokenType tokenType) {
    // given
    setUpParser(input);

    // when
    Target target = underTest.target();

    // then
    assertThat(target).isEqualToComparingFieldByFieldRecursively(new TargetToken(tokenType));
  }

  private static Set<Arguments> targetTokens() {
    return Set.of(
        Arguments.of("@VO2max", TokenType.VO2_MAX),
        Arguments.of("@tempo", TokenType.TEMPO),
        Arguments.of("@LT1", TokenType.LT1),
        Arguments.of("@LT2", TokenType.LT2),
        Arguments.of("@MP", TokenType.MP),
        Arguments.of("@HMP", TokenType.HMP)
    );
  }

  @ParameterizedTest
  @MethodSource("targetTimes")
  void canParseTimeTarget(String input, Time expectedTime) {
    // given
    setUpParser(input);

    // when
    Target target = underTest.target();

    // then
    assertThat(target).isEqualToComparingFieldByFieldRecursively(new TargetValue(Type.TIME, expectedTime));
  }

  private static Set<Arguments> targetTimes() {
    return Set.of(
        Arguments.of("@55s", new Time(0, 0, 55)),
        Arguments.of("@1mn10", new Time(0, 1, 10)),
        Arguments.of("@4:00", new Time(0, 4, 0)),
        Arguments.of("@2h", new Time(2, 0, 0))
    );
  }
}