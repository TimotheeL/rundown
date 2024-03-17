package com.github.rundown.parser;

import com.github.rundown.lexer.Lexer;
import com.github.rundown.parser.Expression.Workout;
import com.github.rundown.util.WorkoutPrinter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WorkoutParserTest {

  private final Lexer lexer = new Lexer();
  private WorkoutParser underTest;

  private void setUpParser(String input) {
    underTest = ParserFactory.createParser(lexer.lex(input));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "10 x 30s, R=30s",
      "6 x strides @3:00/km",
      "R=2mn",
      "5 x 2km , uphill , @4:40/km, R=1km",
      "5 x 2km , uphill @4:40/km, R=1km ; 3 x 100m, R=100m",
      "10 x 1mn @(4:40-4:20)/km, R=1mn"
  })
  void test(String input) {
    // given
    setUpParser(input);

    // when
    Workout workout = underTest.parse();

    // then
    System.out.println(new WorkoutPrinter().print(workout));
  }
}